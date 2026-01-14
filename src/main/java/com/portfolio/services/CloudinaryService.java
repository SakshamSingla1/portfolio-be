package com.portfolio.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {

    Map uploadFile(MultipartFile file) throws IOException;

    Map uploadProfileImage(MultipartFile file) throws IOException;

    Map uploadLogoImage(MultipartFile file) throws IOException;   // ✅ NEW

    void deleteFile(String publicId) throws IOException;
}
