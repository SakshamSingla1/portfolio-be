package com.portfolio.controllers;

import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.LogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logo")
@Tag(name = "Logo", description = "Endpoints for managing company logos")
public class LogoController {

    @Autowired
    private LogoService logoService;

    @Operation(summary = "Get logos", description = "Fetches paginated list of logos with optional search.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<LogoDropdown>>> findLogos(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<LogoDropdown> response = logoService.getAllLogosByPage(pageable, search);
        return ApiResponse.respond(response, "Logos fetched successfully", "Failed to fetch logos");
    }
}
