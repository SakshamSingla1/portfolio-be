package com.portfolio.controllers;

import com.portfolio.dtos.ProfileSubscription.ProfileSubscriptionRequestDTO;
import com.portfolio.dtos.ProfileSubscription.ProfileSubscriptionResponseDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileSubscriptionService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/profile-subscriptions")
@Tag(name = "Profile Subscriptions", description = "APIs for reading and assigning a profile's subscription plan")
@RequiredArgsConstructor
public class ProfileSubscriptionController {

    private final ProfileSubscriptionService profileSubscriptionService;
    private final Helper helper;

    @Operation(summary = "Get my subscription", description = "Fetches the current authenticated profile's active subscription plan.")
    @GetMapping("/me")
    public ResponseEntity<ResponseModel<ProfileSubscriptionResponseDTO>> getMySubscription(
            @RequestHeader(value = "Authorization", required = false) String auth) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        ProfileSubscriptionResponseDTO responseDTO = profileSubscriptionService.getActiveSubscription(profileId);
        return ApiResponse.respond(responseDTO, "Subscription fetched successfully", "Failed to fetch subscription");
    }

    @Operation(summary = "Get subscription by profile ID", description = "Fetches a specific profile's active subscription plan. Requires SUPER_ADMIN role.")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{profileId}")
    public ResponseEntity<ResponseModel<ProfileSubscriptionResponseDTO>> getSubscriptionByProfileId(
            @PathVariable Long profileId) throws GenericException {
        ProfileSubscriptionResponseDTO responseDTO = profileSubscriptionService.getActiveSubscription(profileId);
        return ApiResponse.respond(responseDTO, "Subscription fetched successfully", "Failed to fetch subscription");
    }

    @Operation(summary = "Assign subscription plan", description = "Assigns or changes a profile's subscription plan. Requires SUPER_ADMIN role.")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{profileId}")
    public ResponseEntity<ResponseModel<ProfileSubscriptionResponseDTO>> assignPlan(
            @PathVariable Long profileId,
            @Valid @RequestBody ProfileSubscriptionRequestDTO requestDTO) throws GenericException {
        ProfileSubscriptionResponseDTO responseDTO = profileSubscriptionService.assignPlan(
                profileId, requestDTO.getPlanId(), requestDTO.getBillingCycle(), requestDTO.isAutoRenew());
        return ApiResponse.respond(responseDTO, "Subscription plan assigned successfully", "Failed to assign subscription plan");
    }
}
