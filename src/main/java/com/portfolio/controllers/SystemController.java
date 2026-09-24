package com.portfolio.controllers;

import com.portfolio.dtos.Deployment.DeploymentNotificationRequestDTO;
import com.portfolio.dtos.Deployment.DeploymentNotificationResponseDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.DeploymentNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Operational/ops endpoints — deployment notifications, health-adjacent admin actions, etc. */
@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemController {

    private final DeploymentNotificationService deploymentNotificationService;

    @Operation(
            summary = "Notify SUPER_ADMINs of a successful backend deployment",
            description = "Emails every ACTIVE SUPER_ADMIN profile to let them know the backend deployed "
                    + "successfully. Meant to be called as the last step of a deploy pipeline, or manually "
                    + "after a release. Request body is optional — environment defaults to \"production\" "
                    + "and version to \"unspecified\" when omitted. Requires SUPER_ADMIN role."
    )
    @PostMapping("/notify-deployment")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseModel<DeploymentNotificationResponseDTO>> notifyDeployment(
            @RequestBody(required = false) DeploymentNotificationRequestDTO request) throws GenericException {
        DeploymentNotificationResponseDTO response = deploymentNotificationService.notifySuperAdminsOfDeployment(request);
        return ApiResponse.respond(response, "Deployment notification sent", "Failed to send deployment notification");
    }
}
