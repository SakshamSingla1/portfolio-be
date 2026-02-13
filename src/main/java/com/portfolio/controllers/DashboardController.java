package com.portfolio.controllers;

import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{profileId}")
    public ResponseEntity<ResponseModel<DashboardSummaryDTO>> getDashboardSummary(@PathVariable String profileId) {
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(profileId);
        return ApiResponse.respond(result,ApiResponse.SUCCESS,ApiResponse.FAILED);
    }

}
