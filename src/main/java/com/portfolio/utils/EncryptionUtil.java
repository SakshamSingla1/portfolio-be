package com.portfolio.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    @Value("${ENCRYPTION_KEY:#{null}}")
    private String encryptionKeyBase64;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    private SecretKey getKey() {
        String keySource = (encryptionKeyBase64 != null && !encryptionKeyBase64.isBlank())
                ? encryptionKeyBase64 : jwtSecret;
        // Use first 32 bytes of the key (256-bit AES)
        byte[] keyBytes = Base64.getDecoder().decode(keySource);
        byte[] aesKey = new byte[32];
        System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, 32));
        return new SecretKeySpec(aesKey, "AES");
    }

    public String encrypt(String plaintext) {
        if (plaintext == null) return null;
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
            ByteBuffer buf = ByteBuffer.allocate(iv.length + ciphertext.length);
            buf.put(iv);
            buf.put(ciphertext);
            return Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String ciphertext) {
        if (ciphertext == null) return null;
        try {
            byte[] decoded = Base64.getDecoder().decode(ciphertext);
            ByteBuffer buf = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            buf.get(iv);
            byte[] encrypted = new byte[buf.remaining()];
            buf.get(encrypted);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            // If decryption fails, the value may be stored unencrypted (migration case)
            // Return as-is so existing tokens still work during transition
            return ciphertext;
        }
    }
}
