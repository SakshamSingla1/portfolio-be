package com.portfolio.controllers;

import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.LogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logo")
public class LogoController {

    @Autowired
    private LogoService logoService;

    /**
     * GET /api/v1/logo
     * Returns paginated list of logos with optional search by name
     */
    @GetMapping
    public ResponseEntity<ResponseModel<Page<LogoDropdown>>> findLogos(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<LogoDropdown> response = logoService.getAllLogosByPage(pageable, search);
        return ApiResponse.respond(response, "Logos fetched successfully", "Failed to fetch logos");
    }
}
