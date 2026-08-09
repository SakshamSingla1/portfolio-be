package com.portfolio.controllers;

import com.portfolio.dao.testimonial.TestimonialDao;
import com.portfolio.dtos.Testimonial.TestimonialRequestDTO;
import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.dtos.Common.BulkIdsRequest;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.entities.Testimonial;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.TestimonialService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/testimonials")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TestimonialController {

    private final TestimonialService testimonialService;
    private final TestimonialDao testimonialDao;
    private final Helper helper;

    @Operation(summary = "Create testimonial", description = "Creates a new testimonial record for the authenticated user's profile.")
    @PostMapping
    public ResponseEntity<ResponseModel<TestimonialResponseDTO>> createTestimonial(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody TestimonialRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        TestimonialResponseDTO response = testimonialService.createTestimonial(dto);
        return ApiResponse.respond(response, "Testimonial created successfully", "Failed to create Testimonial");
    }

    @Operation(summary = "Update testimonial", description = "Updates an existing testimonial record identified by its ID for the authenticated user's profile.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<TestimonialResponseDTO>> updateTestimonial(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id, 
            @Valid @RequestBody TestimonialRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        TestimonialResponseDTO response = testimonialService.updateTestimonial(id, dto);
        return ApiResponse.respond(response, "Testimonial updated successfully", "Failed to update Testimonial");
    }

    @Operation(summary = "Get testimonial by ID", description = "Retrieves a single testimonial record by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<TestimonialResponseDTO>> getTestimonialById(@PathVariable Long id) throws GenericException {
        TestimonialResponseDTO response = testimonialService.getTestimonialById(id);
        return ApiResponse.respond(response, "Testimonial fetched successfully", "Failed to fetch Testimonial");
    }

    @Operation(summary = "Get all testimonials", description = "Returns a paginated list of testimonial records for the authenticated user's profile, with optional keyword search and configurable sort field and direction.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<TestimonialResponseDTO>>> getAll(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "order") String sortBy,
            Pageable pageable
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<TestimonialResponseDTO> page = testimonialService.getByProfile(profileId, search, sortDir, sortBy, pageable);
        return ApiResponse.respond(page, "Testimonial fetched successfully", "Failed to fetch Testimonial");
    }

    @Operation(summary = "Delete testimonial", description = "Permanently deletes the testimonial record identified by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteTestimonial(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Testimonial testimonial = testimonialDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TESTIMONIAL_NOT_FOUND, "Testimonial not found"));
        if (!testimonial.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.FORBIDDEN, "You do not have permission to delete this testimonial");
        }
        testimonialService.deleteById(id);
        return ApiResponse.successResponse(null, "Testimonial deleted successfully");
    }

    @Operation(summary = "Bulk delete testimonials", description = "Permanently deletes multiple testimonial records at once, restricted to ones owned by the authenticated profile. Unknown/invalid/not-owned ids are skipped rather than failing the whole batch.")
    @DeleteMapping("/bulk")
    public ResponseEntity<ResponseModel<Integer>> bulkDeleteTestimonials(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody BulkIdsRequest request) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        List<Long> ownedIds = request.getIds().stream()
                .filter(id -> testimonialDao.findById(id).map(t -> t.getProfileId().equals(profileId)).orElse(false))
                .toList();
        int deleted = testimonialService.bulkDeleteByIds(ownedIds);
        return ApiResponse.respond(deleted, deleted + " testimonial(s) deleted successfully", "Failed to bulk delete testimonials");
    }

    @Operation(summary = "Upload testimonial author image", description = "Uploads an author image file for a testimonial and returns the stored image URL for the authenticated user's profile.")
    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadImage(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam("file") MultipartFile file
    ) throws IOException, GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(testimonialService.uploadImage(profileId, file), "Profile image uploaded successfully", "Failed to upload profile image");
    }
}
