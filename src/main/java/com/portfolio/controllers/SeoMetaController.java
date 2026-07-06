package com.portfolio.controllers;

import com.portfolio.dtos.SeoMeta.SeoMetaRequestDTO;
import com.portfolio.dtos.SeoMeta.SeoMetaResponseDTO;
import com.portfolio.enums.PageKeyEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SeoMetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/seo-meta")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "SEO Meta", description = "Manage per-page SEO metadata")
public class SeoMetaController {

    private final SeoMetaService seoMetaService;

    @Operation(summary = "Get all SEO meta entries for the authenticated profile", description = "Returns all SEO metadata entries for every page belonging to the authenticated user's profile.")
    @GetMapping
    public ResponseEntity<ResponseModel<List<SeoMetaResponseDTO>>> getAll(
            @RequestHeader("Authorization") String auth) throws GenericException {
        return ApiResponse.respond(seoMetaService.getAllByProfile(auth), ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get SEO meta for a specific page key", description = "Returns the SEO metadata for a specific page (e.g. HOME, ABOUT, PROJECTS) for the authenticated user's profile.")
    @GetMapping("/{pageKey}")
    public ResponseEntity<ResponseModel<SeoMetaResponseDTO>> getByPageKey(
            @RequestHeader("Authorization") String auth,
            @PathVariable PageKeyEnum pageKey) throws GenericException {
        return ApiResponse.respond(seoMetaService.getByPageKey(auth, pageKey), ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Create or update SEO meta for a page", description = "Creates or updates the SEO metadata for a specific page. If an entry already exists for the page key, it is updated; otherwise a new one is created.")
    @PutMapping
    public ResponseEntity<ResponseModel<SeoMetaResponseDTO>> upsert(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody SeoMetaRequestDTO dto) throws GenericException {
        return ApiResponse.respond(seoMetaService.upsert(auth, dto), ApiResponse.SUCCESS, ApiResponse.FAILED);
    }
}
