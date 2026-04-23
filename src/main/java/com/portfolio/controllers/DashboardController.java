package com.portfolio.controllers;

import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.DashboardService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final Helper helper;

    @GetMapping
    public ResponseEntity<ResponseModel<DashboardSummaryDTO>> getDashboardSummary(@RequestHeader("Authorization") String auth) throws GenericException {
        String profileId = helper.getProfileIdFromHeader(auth);
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(profileId);
        return ApiResponse.respond(result,ApiResponse.SUCCESS,ApiResponse.FAILED);
    }

}
