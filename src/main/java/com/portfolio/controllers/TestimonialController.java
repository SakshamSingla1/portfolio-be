package com.portfolio.controllers;

import com.portfolio.dtos.Testimonial.TestimonialRequestDTO;
import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.TestimonialService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/testimonials")
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialService testimonialService;
    private final Helper helper;

    @PostMapping
    public ResponseEntity<ResponseModel<TestimonialResponseDTO>> createTestimonial(
            @RequestHeader("Authorization") String auth,
            @RequestBody TestimonialRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        TestimonialResponseDTO response = testimonialService.createTestimonial(dto);
        return ApiResponse.respond(response, "Testimonial created successfully", "Failed to create Testimonial");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<TestimonialResponseDTO>> updateTestimonial(
            @RequestHeader("Authorization") String auth,
            @PathVariable String id, 
            @RequestBody TestimonialRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        TestimonialResponseDTO response = testimonialService.updateTestimonial(id, dto);
        return ApiResponse.respond(response, "Testimonial updated successfully", "Failed to update Testimonial");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<TestimonialResponseDTO>> getTestimonialById(@PathVariable String id) throws GenericException {
        TestimonialResponseDTO response = testimonialService.getTestimonialById(id);
        return ApiResponse.respond(response, "Testimonial fetched successfully", "Failed to fetch Testimonial");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<TestimonialResponseDTO>>> getMyTestimonials(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "order") String sortBy,
            Pageable pageable
    ) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        Page<TestimonialResponseDTO> page = testimonialService.getByProfile(profileId, search, sortDir, sortBy, pageable);
        return ApiResponse.respond(page, "Testimonial fetched successfully", "Failed to fetch Testimonial");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteTestimonial(@PathVariable String id) throws GenericException {
        testimonialService.deleteById(id);
        return ApiResponse.respond(null, "Testimonial deleted successfully", "Failed to delete Testimonial");
    }

    @Operation(summary = "Upload Image")
    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadImage(
            @RequestHeader("Authorization") String auth,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(testimonialService.uploadImage(profileId, file), "Profile image uploaded successfully", "Failed to upload profile image");
    }
}
