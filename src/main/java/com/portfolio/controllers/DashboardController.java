package com.portfolio.controllers;

import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.DashboardService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final Helper helper;

    @Operation(summary = "Get dashboard summary", description = "Returns aggregated counts and statistics for the authenticated user's profile including experience, education, skills, projects, achievements, certifications, and contact messages.")
    @GetMapping
    public ResponseEntity<ResponseModel<DashboardSummaryDTO>> getDashboardSummary(@RequestHeader("Authorization") String auth) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(profileId);
        return ApiResponse.respond(result,ApiResponse.SUCCESS,ApiResponse.FAILED);
    }

}
