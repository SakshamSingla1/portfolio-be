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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seo-meta")
@RequiredArgsConstructor
@Tag(name = "SEO Meta", description = "Manage per-page SEO metadata")
public class SeoMetaController {

    private final SeoMetaService seoMetaService;

    @Operation(summary = "Get all SEO meta entries for the authenticated profile")
    @GetMapping
    public ResponseEntity<ResponseModel<List<SeoMetaResponseDTO>>> getAll(
            @RequestHeader("Authorization") String auth) throws GenericException {
        return ApiResponse.respond(seoMetaService.getAllByProfile(auth), ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get SEO meta for a specific page key")
    @GetMapping("/{pageKey}")
    public ResponseEntity<ResponseModel<SeoMetaResponseDTO>> getByPageKey(
            @RequestHeader("Authorization") String auth,
            @PathVariable PageKeyEnum pageKey) throws GenericException {
        return ApiResponse.respond(seoMetaService.getByPageKey(auth, pageKey), ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Create or update SEO meta for a page")
    @PutMapping
    public ResponseEntity<ResponseModel<SeoMetaResponseDTO>> upsert(
            @RequestHeader("Authorization") String auth,
            @RequestBody SeoMetaRequestDTO dto) throws GenericException {
        return ApiResponse.respond(seoMetaService.upsert(auth, dto), ApiResponse.SUCCESS, ApiResponse.FAILED);
    }
}
