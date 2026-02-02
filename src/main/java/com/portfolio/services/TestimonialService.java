package com.portfolio.services;

import com.portfolio.dtos.Testimonial.TestimonialRequestDTO;
import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TestimonialService {
    TestimonialResponseDTO createTestimonial(TestimonialRequestDTO TestimonialRequestDTO) throws GenericException;
    TestimonialResponseDTO updateTestimonial(String id, TestimonialRequestDTO TestimonialDTO) throws GenericException;
    TestimonialResponseDTO getTestimonialById(String id) throws GenericException;
    Page<TestimonialResponseDTO> getByProfile(String profileId, String search, String sortDir, String sortBy, Pageable pageable);
    Void deleteById(String id) throws GenericException;
    ImageUploadResponse uploadImage(String id, MultipartFile file) throws GenericException, IOException;
    List<TestimonialResponseDTO> getByProfile(String profileId);
}
