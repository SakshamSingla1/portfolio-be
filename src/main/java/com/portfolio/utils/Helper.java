package com.portfolio.utils;

import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class Helper {

    private final JwtUtil jwtUtil;

    public String generateRawOtp() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    public String extractEmailFromHeader(String header) throws GenericException {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new GenericException(
                    ExceptionCodeEnum.UNAUTHORIZED,
                    "Invalid authorization header"
            );
        }
        String token = header.substring(7);
        return jwtUtil.extractEmail(token);
    }
}
