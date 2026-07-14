package com.portfolio.controllers;

import com.portfolio.dtos.TestimonialLink.CreateTestimonialLinkRequest;
import com.portfolio.dtos.TestimonialLink.TestimonialLinkResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.TestimonialLinkService;
import com.portfolio.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/testimonial-links")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TestimonialLinkController {

    private final TestimonialLinkService testimonialLinkService;
    private final Helper helper;

    @PostMapping("/")
    public ResponseEntity<ResponseModel<TestimonialLinkResponse>> createLink(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody CreateTestimonialLinkRequest req) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        TestimonialLinkResponse response = testimonialLinkService.createLink(profileId, req);
        return ApiResponse.successResponse(response, "Testimonial request link created successfully");
    }

    @GetMapping("/")
    public ResponseEntity<ResponseModel<List<TestimonialLinkResponse>>> getLinks(
            @RequestHeader("Authorization") String auth) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        List<TestimonialLinkResponse> links = testimonialLinkService.getLinks(profileId);
        return ApiResponse.successResponse(links, "Testimonial request links fetched successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> revokeLink(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        testimonialLinkService.revokeLink(profileId, id);
        return ApiResponse.successResponse(null, "Testimonial request link revoked successfully");
    }
}
