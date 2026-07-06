package com.portfolio.controllers;

import com.portfolio.dtos.NotificationTemplates.NTRequestDTO;
import com.portfolio.dtos.NotificationTemplates.NTResponseDTO;
import com.portfolio.dtos.NotificationTemplates.NotificationTemplateListResponseDTO;
import com.portfolio.dtos.NotificationTemplates.NotificationTemplateVariablesListResponseDTO;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.NTService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NTController {

    private final NTService ntService;

    // ── Notification Templates ────────────────────────────────────────────

    @Operation(summary = "Create notification template", description = "Creates a new notification template for email, SMS, or WhatsApp messaging.")
    @PostMapping("/api/v1/notification-templates")
    public ResponseEntity<ResponseModel<NotificationTemplate>> create(
            @Valid @RequestBody NTRequestDTO dto) throws GenericException {
        return ApiResponse.respond(ntService.createNT(dto),
                "Notification template created", "Failed to create notification template");
    }

    @Operation(summary = "Get notification template by ID", description = "Fetches a single notification template by its ID.")
    @GetMapping("/api/v1/notification-templates/{id}")
    public ResponseEntity<ResponseModel<NTResponseDTO>> findById(
            @PathVariable Long id) throws GenericException {
        return ApiResponse.respond(ntService.findNTById(id),
                "Notification template fetched", "Failed to fetch notification template");
    }

    @Operation(summary = "Get all notification templates", description = "Returns a paginated list of notification templates with optional search and template group filter.")
    @GetMapping("/api/v1/notification-templates")
    public ResponseEntity<ResponseModel<Page<NotificationTemplateListResponseDTO>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String templateGroupIdString,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.respond(
                ntService.getAllByCriteria(search, templateGroupIdString, pageable),
                "Notification templates fetched", "Failed to fetch notification templates");
    }

    @Operation(summary = "Update notification template", description = "Updates an existing notification template by ID.")
    @PutMapping("/api/v1/notification-templates/{id}")
    public ResponseEntity<ResponseModel<NotificationTemplate>> update(
            @PathVariable Long id,
            @Valid @RequestBody NTRequestDTO dto) throws GenericException {
        return ApiResponse.respond(ntService.updateNT(id, dto),
                "Notification template updated", "Failed to update notification template");
    }

    // ── Notification Template Variables ──────────────────────────────────

    @Operation(summary = "Get template variables", description = "Returns a paginated list of available template variables that can be used in notification template bodies.")
    @GetMapping("/api/v1/notification-template-variables")
    public ResponseEntity<ResponseModel<Page<NotificationTemplateVariablesListResponseDTO>>> getVariables(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 100) Pageable pageable) {
        return ApiResponse.respond(
                ntService.getVariablesByCriteria(search, pageable),
                "Template variables fetched", "Failed to fetch template variables");
    }
}
