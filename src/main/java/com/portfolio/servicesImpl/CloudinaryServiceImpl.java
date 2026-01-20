package com.portfolio.servicesImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.portfolio.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public Map uploadFile(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "portfolio/documents",
                        "resource_type", "raw",
                        "access_mode", "authenticated",
                        "secure", true
                )
        );
    }

        @Override
        public Map uploadProfileImage(MultipartFile file) throws IOException {
            validateImage(file);

            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "portfolio/profile",
                            "resource_type", "image",
                            "secure", true
                    )
            );
        }

        @Override
        public Map uploadLogoImage(MultipartFile file) throws IOException {
            validateImage(file);

            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "portfolio/logo",
                            "resource_type", "image",
                            "secure", true
                    )
            );
        }

        @Override
        public void deleteFile(String publicId) throws IOException {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }

        private void validateImage(MultipartFile file) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File is required");
            }
            if (!file.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }
        }
    }
