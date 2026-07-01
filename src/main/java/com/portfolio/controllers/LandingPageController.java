package com.portfolio.controllers;

import com.portfolio.dtos.Landing.LandingAudienceCardRequest;
import com.portfolio.dtos.Landing.LandingConfigRequest;
import com.portfolio.dtos.Landing.LandingFaqRequest;
import com.portfolio.dtos.Landing.LandingFeatureRequest;
import com.portfolio.dtos.Landing.LandingHowToUseStepRequest;
import com.portfolio.dtos.Landing.LandingTestimonialRequest;
import com.portfolio.dtos.LandingPage.LandingAudienceCardResponse;
import com.portfolio.dtos.LandingPage.LandingConfigResponse;
import com.portfolio.dtos.LandingPage.LandingFaqResponse;
import com.portfolio.dtos.LandingPage.LandingFeatureResponse;
import com.portfolio.dtos.LandingPage.LandingHowToUseStepResponse;
import com.portfolio.dtos.LandingPage.LandingPageResponse;
import com.portfolio.dtos.LandingPage.LandingTestimonialResponse;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.LandingPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/landing")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Landing Page", description = "Admin endpoints for managing landing page content")
@RequiredArgsConstructor
public class LandingPageController {

    private final LandingPageService landingPageService;

    // ── Public (no role check — URL is whitelisted in SecurityConfig) ─────

    @Operation(summary = "Get public landing page — no auth required", description = "Returns the complete public landing page data including config, features, FAQs, how-to-use steps, audience cards, and testimonials. No authentication required.")
    @GetMapping("/page")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseModel<LandingPageResponse>> getPublicLandingPage() {
        try {
            return ApiResponse.successResponse(landingPageService.getPublicLandingPage(), "Landing page fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    // ── Config ────────────────────────────────────────────────────────────

    @Operation(summary = "Get landing config", description = "Returns the current landing page configuration (hero section, CTA section). Requires SUPER_ADMIN role.")
    @GetMapping("/config")
    public ResponseEntity<ResponseModel<LandingConfigResponse>> getConfig() {
        try {
            return ApiResponse.successResponse(landingPageService.getConfig(), "Config fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Update landing config", description = "Creates or updates the singleton landing page configuration with hero and CTA content. Requires SUPER_ADMIN role.")
    @PutMapping("/config")
    public ResponseEntity<ResponseModel<LandingConfigResponse>> updateConfig(@RequestBody LandingConfigRequest request) {
        try {
            return ApiResponse.successResponse(landingPageService.updateConfig(request), "Config updated successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    // ── Features ──────────────────────────────────────────────────────────

    @Operation(summary = "Get all features", description = "Returns all landing page feature cards. Requires SUPER_ADMIN role.")
    @GetMapping("/features")
    public ResponseEntity<ResponseModel<List<LandingFeatureResponse>>> getFeatures() {
        try {
            return ApiResponse.successResponse(landingPageService.getFeatures(), "Features fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Create a feature", description = "Creates a new feature card for the landing page. Requires SUPER_ADMIN role.")
    @PostMapping("/features")
    public ResponseEntity<ResponseModel<LandingFeatureResponse>> createFeature(@RequestBody LandingFeatureRequest request) {
        try {
            return ApiResponse.createSuccess(landingPageService.createFeature(request), "Feature created successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Update a feature", description = "Updates an existing landing page feature card by ID. Requires SUPER_ADMIN role.")
    @PutMapping("/features/{id}")
    public ResponseEntity<ResponseModel<LandingFeatureResponse>> updateFeature(
            @PathVariable Long id, @RequestBody LandingFeatureRequest request) {
        try {
            return ApiResponse.successResponse(landingPageService.updateFeature(id, request), "Feature updated successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Delete a feature", description = "Permanently deletes a landing page feature card by ID. Requires SUPER_ADMIN role.")
    @DeleteMapping("/features/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteFeature(@PathVariable Long id) {
        try {
            landingPageService.deleteFeature(id);
            return ApiResponse.successResponse(null, "Feature deleted successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    // ── FAQs ──────────────────────────────────────────────────────────────

    @Operation(summary = "Get all FAQs", description = "Returns all FAQ entries for the landing page. Requires SUPER_ADMIN role.")
    @GetMapping("/faqs")
    public ResponseEntity<ResponseModel<List<LandingFaqResponse>>> getFaqs() {
        try {
            return ApiResponse.successResponse(landingPageService.getFaqs(), "FAQs fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Create a FAQ", description = "Creates a new FAQ entry for the landing page. Requires SUPER_ADMIN role.")
    @PostMapping("/faqs")
    public ResponseEntity<ResponseModel<LandingFaqResponse>> createFaq(@RequestBody LandingFaqRequest request) {
        try {
            return ApiResponse.createSuccess(landingPageService.createFaq(request), "FAQ created successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Update a FAQ", description = "Updates an existing FAQ entry by ID. Requires SUPER_ADMIN role.")
    @PutMapping("/faqs/{id}")
    public ResponseEntity<ResponseModel<LandingFaqResponse>> updateFaq(
            @PathVariable Long id, @RequestBody LandingFaqRequest request) {
        try {
            return ApiResponse.successResponse(landingPageService.updateFaq(id, request), "FAQ updated successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Delete a FAQ", description = "Permanently deletes a FAQ entry by ID. Requires SUPER_ADMIN role.")
    @DeleteMapping("/faqs/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteFaq(@PathVariable Long id) {
        try {
            landingPageService.deleteFaq(id);
            return ApiResponse.successResponse(null, "FAQ deleted successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    // ── How-To-Use Steps ─────────────────────────────────────────────────

    @Operation(summary = "Get all steps", description = "Returns all how-to-use step cards for the landing page. Requires SUPER_ADMIN role.")
    @GetMapping("/steps")
    public ResponseEntity<ResponseModel<List<LandingHowToUseStepResponse>>> getSteps() {
        try {
            return ApiResponse.successResponse(landingPageService.getSteps(), "Steps fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Create a step", description = "Creates a new how-to-use step card with bullets. Requires SUPER_ADMIN role.")
    @PostMapping("/steps")
    public ResponseEntity<ResponseModel<LandingHowToUseStepResponse>> createStep(@RequestBody LandingHowToUseStepRequest request) {
        try {
            return ApiResponse.createSuccess(landingPageService.createStep(request), "Step created successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Update a step", description = "Updates an existing how-to-use step card by ID. Requires SUPER_ADMIN role.")
    @PutMapping("/steps/{id}")
    public ResponseEntity<ResponseModel<LandingHowToUseStepResponse>> updateStep(
            @PathVariable Long id, @RequestBody LandingHowToUseStepRequest request) {
        try {
            return ApiResponse.successResponse(landingPageService.updateStep(id, request), "Step updated successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Delete a step", description = "Permanently deletes a how-to-use step card by ID. Requires SUPER_ADMIN role.")
    @DeleteMapping("/steps/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteStep(@PathVariable Long id) {
        try {
            landingPageService.deleteStep(id);
            return ApiResponse.successResponse(null, "Step deleted successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    // ── Audience Cards ───────────────────────────────────────────────────

    @Operation(summary = "Get all audience cards", description = "Returns all audience target cards for the landing page. Requires SUPER_ADMIN role.")
    @GetMapping("/audience")
    public ResponseEntity<ResponseModel<List<LandingAudienceCardResponse>>> getAudienceCards() {
        try {
            return ApiResponse.successResponse(landingPageService.getAudienceCards(), "Audience cards fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Create an audience card", description = "Creates a new audience target card for the landing page. Requires SUPER_ADMIN role.")
    @PostMapping("/audience")
    public ResponseEntity<ResponseModel<LandingAudienceCardResponse>> createAudienceCard(@RequestBody LandingAudienceCardRequest request) {
        try {
            return ApiResponse.createSuccess(landingPageService.createAudienceCard(request), "Audience card created successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Update an audience card", description = "Updates an existing audience target card by ID. Requires SUPER_ADMIN role.")
    @PutMapping("/audience/{id}")
    public ResponseEntity<ResponseModel<LandingAudienceCardResponse>> updateAudienceCard(
            @PathVariable Long id, @RequestBody LandingAudienceCardRequest request) {
        try {
            return ApiResponse.successResponse(landingPageService.updateAudienceCard(id, request), "Audience card updated successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Delete an audience card", description = "Permanently deletes an audience target card by ID. Requires SUPER_ADMIN role.")
    @DeleteMapping("/audience/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteAudienceCard(@PathVariable Long id) {
        try {
            landingPageService.deleteAudienceCard(id);
            return ApiResponse.successResponse(null, "Audience card deleted successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    // ── Testimonials ──────────────────────────────────────────────────────

    @Operation(summary = "Get all testimonials", description = "Returns all landing page testimonials. Requires SUPER_ADMIN role.")
    @GetMapping("/testimonials")
    public ResponseEntity<ResponseModel<List<LandingTestimonialResponse>>> getTestimonials() {
        try {
            return ApiResponse.successResponse(landingPageService.getTestimonials(), "Testimonials fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Create a testimonial", description = "Creates a new testimonial entry for the landing page. Requires SUPER_ADMIN role.")
    @PostMapping("/testimonials")
    public ResponseEntity<ResponseModel<LandingTestimonialResponse>> createTestimonial(@RequestBody LandingTestimonialRequest request) {
        try {
            return ApiResponse.createSuccess(landingPageService.createTestimonial(request), "Testimonial created successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Update a testimonial", description = "Updates an existing landing page testimonial by ID. Requires SUPER_ADMIN role.")
    @PutMapping("/testimonials/{id}")
    public ResponseEntity<ResponseModel<LandingTestimonialResponse>> updateTestimonial(
            @PathVariable Long id, @RequestBody LandingTestimonialRequest request) {
        try {
            return ApiResponse.successResponse(landingPageService.updateTestimonial(id, request), "Testimonial updated successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Delete a testimonial", description = "Permanently deletes a landing page testimonial by ID. Requires SUPER_ADMIN role.")
    @DeleteMapping("/testimonials/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteTestimonial(@PathVariable Long id) {
        try {
            landingPageService.deleteTestimonial(id);
            return ApiResponse.successResponse(null, "Testimonial deleted successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }
}
