package com.portfolio.controllers;

import com.portfolio.dtos.Help.HelpFaqRequest;
import com.portfolio.dtos.Help.HelpFaqResponse;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.HelpFaqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/help-faqs")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Help FAQs", description = "Help-tab FAQ content — readable by any authenticated user, managed by Super Admins")
@RequiredArgsConstructor
public class HelpFaqController {

    private final HelpFaqService helpFaqService;

    @Operation(summary = "Get active FAQs", description = "Returns active Help FAQs, sorted for display. Available to any authenticated user.")
    @GetMapping
    public ResponseEntity<ResponseModel<List<HelpFaqResponse>>> getFaqs() {
        try {
            return ApiResponse.successResponse(helpFaqService.getActiveFaqs(), "FAQs fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Get all FAQs for management", description = "Returns all Help FAQs including inactive ones. Requires SUPER_ADMIN role.")
    @GetMapping("/manage")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<List<HelpFaqResponse>>> getFaqsForManagement() {
        try {
            return ApiResponse.successResponse(helpFaqService.getAllFaqsForManagement(), "FAQs fetched successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Create a FAQ", description = "Creates a new Help FAQ entry. Requires SUPER_ADMIN role.")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<HelpFaqResponse>> createFaq(@Valid @RequestBody HelpFaqRequest request) {
        try {
            return ApiResponse.createSuccess(helpFaqService.createFaq(request), "FAQ created successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Update a FAQ", description = "Updates an existing Help FAQ by ID. Requires SUPER_ADMIN role.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<HelpFaqResponse>> updateFaq(
            @PathVariable Long id, @Valid @RequestBody HelpFaqRequest request) {
        try {
            return ApiResponse.successResponse(helpFaqService.updateFaq(id, request), "FAQ updated successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }

    @Operation(summary = "Delete a FAQ", description = "Permanently deletes a Help FAQ by ID. Requires SUPER_ADMIN role.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<Void>> deleteFaq(@PathVariable Long id) {
        try {
            helpFaqService.deleteFaq(id);
            return ApiResponse.successResponse(null, "FAQ deleted successfully");
        } catch (Exception e) {
            return ApiResponse.exceptionResponse(e.getMessage());
        }
    }
}
