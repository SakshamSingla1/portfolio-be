package com.portfolio.controllers;

import com.portfolio.dtos.Publication.PublicationRequestDTO;
import com.portfolio.dtos.Publication.PublicationResponseDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.PublicationService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/publications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PublicationController {

    private final PublicationService publicationService;
    private final Helper helper;

    @Operation(summary = "Create publication", description = "Creates a new publication record for the authenticated user's profile.")
    @PostMapping
    public ResponseEntity<ResponseModel<PublicationResponseDTO>> create(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody PublicationRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        PublicationResponseDTO response = publicationService.create(dto);
        return ApiResponse.respond(response, "Publication created successfully", "Failed to create publication");
    }

    @Operation(summary = "Update publication", description = "Updates an existing publication record identified by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<PublicationResponseDTO>> update(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id,
            @Valid @RequestBody PublicationRequestDTO dto) throws GenericException {
        dto.setProfileId(helper.getProfileIdFromHeader(auth));
        PublicationResponseDTO response = publicationService.update(id, dto);
        return ApiResponse.respond(response, "Publication updated successfully", "Failed to update publication");
    }

    @Operation(summary = "Get publication by ID", description = "Retrieves a single publication record by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<PublicationResponseDTO>> getById(@PathVariable Long id) throws GenericException {
        PublicationResponseDTO response = publicationService.getById(id);
        return ApiResponse.respond(response, "Publication fetched successfully", "Failed to fetch publication");
    }

    @Operation(summary = "Get all publications", description = "Returns a paginated list of publication records for the authenticated user's profile.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<PublicationResponseDTO>>> getAll(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(required = false) String search,
            Pageable pageable) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<PublicationResponseDTO> page = publicationService.getAll(profileId, search, pageable);
        return ApiResponse.respond(page, "Publications fetched successfully", "Failed to fetch publications");
    }

    @Operation(summary = "Delete publication", description = "Permanently deletes the publication record identified by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteById(@PathVariable Long id) throws GenericException {
        publicationService.deleteById(id);
        return ApiResponse.successResponse(null, "Publication deleted successfully");
    }
}
