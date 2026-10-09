package com.portfolio.controllers;

import com.portfolio.dtos.DashboardDTOs.AnalyticsOverviewDTO;
import com.portfolio.dtos.DashboardDTOs.DashboardSummaryDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.DashboardService;
import com.portfolio.services.PortfolioViewService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    private final DashboardService dashboardService;
    private final PortfolioViewService portfolioViewService;
    private final Helper helper;

    @Operation(summary = "Get dashboard summary", description = "Returns aggregated counts and statistics for the authenticated user's profile including experience, education, skills, projects, achievements, certifications, and contact messages.")
    @GetMapping
    public ResponseEntity<ResponseModel<DashboardSummaryDTO>> getDashboardSummary(@RequestHeader(value = "Authorization", required = false) String auth) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        DashboardSummaryDTO result = dashboardService.getDashboardSummary(profileId);
        return ApiResponse.respond(result,ApiResponse.SUCCESS,ApiResponse.FAILED);
    }

    @Operation(summary = "Get Analytics Dashboard overview", description = "Returns range-aware portfolio view analytics for the authenticated user's profile: totals, period-over-period comparison, daily trend, device/browser/location/referrer breakdowns, recent views, and the fixed 90-day activity heatmap. `range` is one of 7d/30d/90d/custom (defaults to 30d); `startDate`/`endDate` (yyyy-MM-dd) are only used when range=custom.")
    @GetMapping("/view-stats")
    public ResponseEntity<ResponseModel<AnalyticsOverviewDTO>> getAnalyticsOverview(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        AnalyticsOverviewDTO result = portfolioViewService.getViewStatsForRange(profileId, range, startDate, endDate);
        return ApiResponse.respond(result, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

}
