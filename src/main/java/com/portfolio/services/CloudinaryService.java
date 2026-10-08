package com.portfolio.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {

    Map<String, Object> uploadFile(MultipartFile file) throws IOException;

    Map<String, Object> uploadProfileImage(MultipartFile file) throws IOException;

    Map<String, Object> uploadLogoImage(MultipartFile file) throws IOException;

    Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException;

    Map<String, Object> uploadRawDocument(MultipartFile file, String folder) throws IOException;

    /** Uploads raw bytes (no MultipartFile involved) as a "raw" resource -- e.g. a PDF generated
     *  in-memory, with nothing on disk to wrap. */
    Map<String, Object> uploadBytes(byte[] data, String folder) throws IOException;

    void deleteFile(String publicId) throws IOException;
}
