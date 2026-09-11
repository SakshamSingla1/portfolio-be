package com.portfolio.controllers;

import com.portfolio.dtos.SubscriptionPlan.PlanNavLinksRequestDTO;
import com.portfolio.dtos.SubscriptionPlan.SubscriptionPlanRequestDTO;
import com.portfolio.dtos.SubscriptionPlan.SubscriptionPlanResponseDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SubscriptionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('SUPER_ADMIN')")
@RestController
@RequestMapping("/api/v1/subscription-plans")
@Tag(name = "Subscription Plans", description = "APIs for managing subscription plans and their gated nav links")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @Operation(summary = "Create subscription plan", description = "Creates a new subscription plan. Requires SUPER_ADMIN role.")
    @PostMapping
    public ResponseEntity<ResponseModel<SubscriptionPlanResponseDTO>> createPlan(
            @Valid @RequestBody SubscriptionPlanRequestDTO requestDTO) throws GenericException {
        SubscriptionPlanResponseDTO responseDTO = subscriptionPlanService.upsertPlan(null, requestDTO);
        return ApiResponse.respond(responseDTO, "Subscription plan created successfully", "Failed to create subscription plan");
    }

    @Operation(summary = "Update subscription plan", description = "Updates an existing subscription plan by ID. Requires SUPER_ADMIN role.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SubscriptionPlanResponseDTO>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanRequestDTO requestDTO) throws GenericException {
        SubscriptionPlanResponseDTO responseDTO = subscriptionPlanService.upsertPlan(id, requestDTO);
        return ApiResponse.respond(responseDTO, "Subscription plan updated successfully", "Failed to update subscription plan");
    }

    @Operation(summary = "Set plan nav links", description = "Replaces the full set of nav links (modules) included in a plan. Requires SUPER_ADMIN role.")
    @PutMapping("/{id}/nav-links")
    public ResponseEntity<ResponseModel<SubscriptionPlanResponseDTO>> setPlanNavLinks(
            @PathVariable Long id,
            @RequestBody PlanNavLinksRequestDTO requestDTO) throws GenericException {
        SubscriptionPlanResponseDTO responseDTO = subscriptionPlanService.upsertPlanNavLinks(id, requestDTO.getNavLinkIds());
        return ApiResponse.respond(responseDTO, "Plan nav links updated successfully", "Failed to update plan nav links");
    }

    @Operation(summary = "Delete subscription plan", description = "Deletes a subscription plan by ID, provided no profile is currently assigned to it. Requires SUPER_ADMIN role.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deletePlan(@PathVariable Long id) throws GenericException {
        subscriptionPlanService.deletePlan(id);
        return ApiResponse.respond("Subscription plan deleted successfully", "Subscription plan deleted successfully", "Failed to delete subscription plan");
    }

    @Operation(summary = "Get subscription plan by ID", description = "Fetches a single subscription plan along with its included nav links.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SubscriptionPlanResponseDTO>> getPlan(@PathVariable Long id) throws GenericException {
        SubscriptionPlanResponseDTO responseDTO = subscriptionPlanService.getPlanById(id);
        return ApiResponse.respond(responseDTO, "Subscription plan fetched successfully", "Failed to fetch subscription plan");
    }

    @Operation(summary = "Get all subscription plans", description = "Returns a paginated list of subscription plans with optional search and status filter.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<SubscriptionPlanResponseDTO>>> getAllPlans(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable) throws GenericException {
        Page<SubscriptionPlanResponseDTO> responseDTO = subscriptionPlanService.getAllPlans(search, status, pageable);
        return ApiResponse.respond(responseDTO, "Subscription plans fetched successfully", "Failed to fetch subscription plans");
    }
}
