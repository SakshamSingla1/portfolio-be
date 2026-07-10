package com.portfolio.controllers;

import com.portfolio.dtos.Blog.BlogTagRequest;
import com.portfolio.dtos.Blog.BlogTagResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.BlogTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/blog/tags")
@Tag(name = "Blog Tags", description = "APIs for managing Blog Tags")
@RequiredArgsConstructor
public class BlogTagController {

    private final BlogTagService blogTagService;

    @Operation(summary = "Create a blog tag")
    @PostMapping
    public ResponseEntity<ResponseModel<BlogTagResponse>> create(@Valid @RequestBody BlogTagRequest request) {
        try {
            return ApiResponse.successResponse(blogTagService.create(request), "Tag created successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Update a blog tag")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<BlogTagResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BlogTagRequest request) {
        try {
            return ApiResponse.successResponse(blogTagService.update(id, request), "Tag updated successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Get a blog tag by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<BlogTagResponse>> getById(@PathVariable Long id) {
        try {
            return ApiResponse.successResponse(blogTagService.getById(id), "Tag fetched successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Delete a blog tag")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Long id) {
        try {
            return ApiResponse.successResponse(blogTagService.delete(id), "Tag deleted successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Get all blog tags")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<BlogTagResponse>>> getAll(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ApiResponse.successResponse(blogTagService.getAll(search, pageable), "Tags fetched successfully");
    }
}
