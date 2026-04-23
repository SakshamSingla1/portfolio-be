package com.portfolio.controllers;

import com.portfolio.dtos.ColorTheme.ColorThemeRequestDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.enums.*;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.*;
import com.portfolio.services.ColorThemeService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/color-themes")
@RequiredArgsConstructor
public class ColorThemeController {

        private final ColorThemeService colorThemeService;

        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PostMapping
        public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> createTheme(
                        @RequestBody ColorThemeRequestDTO dto) throws GenericException {
                ColorThemeResponseDTO response = colorThemeService.createTheme(dto);
                return ApiResponse.respond(
                                response,
                                "Color theme created successfully",
                                "Failed to create color theme");
        }

        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> updateTheme(
                        @PathVariable String id,
                        @RequestBody ColorThemeRequestDTO dto) throws GenericException {
                ColorThemeResponseDTO response = colorThemeService.updateTheme(id, dto);
                return ApiResponse.respond(
                                response,
                                "Color theme updated successfully",
                                "Failed to update color theme");
        }

        @GetMapping("/{id}")
        public ResponseEntity<ResponseModel<ColorThemeResponseDTO>> getThemeById(
                        @PathVariable String id) throws GenericException {
                ColorThemeResponseDTO response = colorThemeService.getThemeById(id);
                return ApiResponse.respond(
                                response,
                                "Theme fetched successfully",
                                "Failed to fetch theme");
        }

        @GetMapping
        public ResponseEntity<ResponseModel<Page<ColorThemeResponseDTO>>> getAllThemes(Pageable pageable,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String sortBy,
                        @RequestParam(required = false) String sortDir,
                        @RequestParam(required = false) StatusEnum status) {
                Page<ColorThemeResponseDTO> response = colorThemeService.getAllThemes(
                                search,
                                sortBy,
                                sortDir,
                                status,
                                pageable);
                return ApiResponse.respond(
                                response,
                                "Color themes fetched successfully",
                                "Failed to fetch color themes");
        }

        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseModel<String>> deleteTheme(
                        @PathVariable String id) throws GenericException {
                String response = colorThemeService.deleteTheme(id);
                return ApiResponse.respond(
                                response,
                                "Color theme deleted successfully",
                                "Failed to delete color theme");
        }
}
