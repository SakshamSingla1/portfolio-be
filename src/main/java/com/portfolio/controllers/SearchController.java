package com.portfolio.controllers;

import com.portfolio.dtos.Search.SearchResultDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SearchService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final Helper helper;

    @Operation(summary = "Search content", description = "Searches across the authenticated user's skills, projects, experiences, education, certifications, publications, achievements, services, testimonials and blog posts.")
    @GetMapping
    public ResponseEntity<ResponseModel<List<SearchResultDTO>>> search(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam("q") String query
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        List<SearchResultDTO> results = searchService.search(profileId, query);
        return ApiResponse.successResponse(results, "Search results fetched successfully");
    }
}
