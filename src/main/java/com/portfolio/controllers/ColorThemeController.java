package com.portfolio.controllers;

import com.portfolio.dtos.ColorTheme.ColorThemeRequestDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.enums.*;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.*;
import com.portfolio.services.ColorThemeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/color-themes")
@RequiredArgsConstructor
public class ColorThemeController {

        private final ColorThemeService colorThemeService;

        @Operation(summary = "Create color theme", description = "Creates a new color theme. Requires SUPER_ADMIN role.")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PostMapping
        public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> createTheme(
                        @Valid @RequestBody ColorThemeRequestDTO dto) throws GenericException {
                ColorThemeResponseDTO response = colorThemeService.createTheme(dto);
                return ApiResponse.respond(
                                response,
                                "Color theme created successfully",
                                "Failed to create color theme");
        }

        @Operation(summary = "Update color theme", description = "Updates an existing color theme by ID. Requires SUPER_ADMIN role.")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> updateTheme(
                        @PathVariable Long id,
                        @Valid @RequestBody ColorThemeRequestDTO dto) throws GenericException {
                ColorThemeResponseDTO response = colorThemeService.updateTheme(id, dto);
                return ApiResponse.respond(
                                response,
                                "Color theme updated successfully",
                                "Failed to update color theme");
        }

        @Operation(summary = "Get color theme by ID", description = "Fetches a single color theme by its ID.")
        @GetMapping("/{id}")
        public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> getThemeById(
                        @PathVariable Long id) throws GenericException {
                ColorThemeResponseDTO response = colorThemeService.getThemeById(id);
                return ApiResponse.respond(
                                response,
                                "Theme fetched successfully",
                                "Failed to fetch theme");
        }

        @Operation(summary = "Get all color themes", description = "Returns a paginated list of color themes with optional search and status filter.")
        @GetMapping
        public ResponseEntity<ResponseModel<Page<ColorThemeResponseDTO>>> getAllThemes(Pageable pageable,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) StatusEnum status) {
                Page<ColorThemeResponseDTO> response = colorThemeService.getAllThemes(
                                search,
                                status,
                                pageable);
                return ApiResponse.respond(
                                response,
                                "Color themes fetched successfully",
                                "Failed to fetch color themes");
        }

        @Operation(summary = "Delete color theme", description = "Deletes a color theme by ID. Requires SUPER_ADMIN role.")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseModel<String>> deleteTheme(
                        @PathVariable Long id) throws GenericException {
                String response = colorThemeService.deleteTheme(id);
                return ApiResponse.respond(
                                response,
                                "Color theme deleted successfully",
                                "Failed to delete color theme");
        }
}
