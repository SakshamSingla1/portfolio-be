package com.portfolio.controllers;

import com.portfolio.dtos.Blog.BlogPostRequest;
import com.portfolio.dtos.Blog.BlogPostResponse;
import com.portfolio.dtos.Blog.BlogPostSummary;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.enums.BlogStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.BlogPostService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/blog/posts")
@Tag(name = "Blog Posts", description = "APIs for managing Blog Posts")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostService blogPostService;
    private final Helper helper;

    @Operation(summary = "Create a blog post")
    @PostMapping
    public ResponseEntity<ResponseModel<BlogPostResponse>> create(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody BlogPostRequest request) {
        try {
            request.setProfileId(helper.getProfileIdFromHeader(auth));
            return ApiResponse.successResponse(blogPostService.create(request), "Blog post created successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Update a blog post")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<BlogPostResponse>> update(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id,
            @Valid @RequestBody BlogPostRequest request) {
        try {
            request.setProfileId(helper.getProfileIdFromHeader(auth));
            return ApiResponse.successResponse(blogPostService.update(id, request), "Blog post updated successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Get a blog post by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<BlogPostResponse>> getById(@PathVariable Long id) {
        try {
            return ApiResponse.successResponse(blogPostService.getById(id), "Blog post fetched successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Delete a blog post")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Long id) {
        try {
            return ApiResponse.successResponse(blogPostService.delete(id), "Blog post deleted successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "List blog posts for the logged-in profile")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<BlogPostSummary>>> getAll(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) BlogStatusEnum status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Pageable pageable) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<BlogPostSummary> posts = blogPostService.getByProfile(profileId, status, search, sortBy, sortDir, pageable);
        return ApiResponse.successResponse(posts, "Blog posts fetched successfully");
    }

    @Operation(summary = "Publish a blog post")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<ResponseModel<BlogPostResponse>> publish(@PathVariable Long id) {
        try {
            return ApiResponse.successResponse(blogPostService.publish(id), "Blog post published successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Archive a blog post")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<ResponseModel<BlogPostResponse>> archive(@PathVariable Long id) {
        try {
            return ApiResponse.successResponse(blogPostService.archive(id), "Blog post archived successfully");
        } catch (GenericException e) {
            return ApiResponse.failureResponse(null, e.getMessage());
        }
    }

    @Operation(summary = "Upload a cover image for a specific blog post")
    @PostMapping("/{id}/cover")
    public ResponseEntity<ResponseModel<ImageUploadResponse>> uploadCoverImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException, GenericException {
        return ApiResponse.respond(
                blogPostService.uploadCoverImage(id, file),
                "Cover image uploaded successfully",
                "Failed to upload cover image"
        );
    }
}
