package com.portfolio.controllers;

import com.portfolio.dtos.Ai.ContentDraftRequest;
import com.portfolio.dtos.Ai.ContentDraftResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.AiContentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/ai")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AiContentController {

    private final AiContentService aiContentService;

    @Operation(summary = "Draft portfolio content", description = "Generates a bio, project description, or experience description draft from the user's rough notes via Claude.")
    @PostMapping("/draft")
    public ResponseEntity<ResponseModel<ContentDraftResponse>> draft(
            @Valid @RequestBody ContentDraftRequest request) throws GenericException {
        return ApiResponse.respond(aiContentService.draftContent(request),
                "Draft generated successfully", "Failed to generate draft");
    }
}
