package com.portfolio.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {

    Map<String, Object> uploadFile(MultipartFile file) throws IOException;

    Map<String, Object> uploadProfileImage(MultipartFile file) throws IOException;

    Map<String, Object> uploadLogoImage(MultipartFile file) throws IOException;

    Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException;

    void deleteFile(String publicId) throws IOException;
}
