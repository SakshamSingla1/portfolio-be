package com.portfolio.controllers;

import com.portfolio.dtos.LogoRequest;
import com.portfolio.dtos.LogoResponse;
import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.LogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logo")
@Tag(name = "Logo", description = "Endpoints for managing company logos")
@RequiredArgsConstructor
public class LogoController {

    private final LogoService logoService;

    @Operation(summary = "Get logos", description = "Fetches paginated list of logos with optional search.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<LogoDropdown>>> findLogos(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {

        Page<LogoDropdown> response = logoService.getAllLogosByPage(pageable, search, sortDir, sortBy);
        return ApiResponse.respond(response, "Logos fetched successfully", "Failed to fetch logos");
    }

    @Operation(summary = "Get logo by ID", description = "Fetches a single logo by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<LogoResponse>> getLogoById(@PathVariable String id) {
        try {
            LogoResponse response = logoService.getById(id);
            return ApiResponse.respond(response, "Logo fetched successfully", "Failed to fetch logo");
        } catch (Exception e) {
            return ApiResponse.respond(null, "Logo fetched successfully", e.getMessage());
        }
    }

    @Operation(summary = "Create a logo", description = "Creates a new logo")
    @PostMapping
    public ResponseEntity<ResponseModel<LogoResponse>> createLogo(@RequestBody LogoRequest request) {
        try {
            LogoResponse response = logoService.create(request);
            return ApiResponse.respond(response, "Logo created successfully", "Failed to create logo");
        } catch (Exception e) {
            return ApiResponse.respond(null, "Logo created successfully", e.getMessage());
        }
    }

    @Operation(summary = "Update a logo", description = "Updates an existing logo by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<LogoResponse>> updateLogo(
            @PathVariable String id,
            @RequestBody LogoRequest request) {
        try {
            LogoResponse response = logoService.update(id, request);
            return ApiResponse.respond(response, "Logo updated successfully", "Failed to update logo");
        } catch (Exception e) {
            return ApiResponse.respond(null, "Logo updated successfully", e.getMessage());
        }
    }

    @Operation(summary = "Delete a logo", description = "Deletes a logo by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteLogo(@PathVariable String id) {
        try {
            logoService.delete(id);
            return ApiResponse.respond("Deleted logo with ID: " + id, "Logo deleted successfully", "Failed to delete logo");
        } catch (Exception e) {
            return ApiResponse.respond(null, "Logo deleted successfully", e.getMessage());
        }
    }
}
