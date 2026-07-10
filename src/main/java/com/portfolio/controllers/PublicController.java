package com.portfolio.controllers;

import com.portfolio.dtos.Blog.BlogPostResponse;
import com.portfolio.dtos.Blog.BlogPostSummary;
import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewRequest;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.BlogPostService;
import com.portfolio.services.FileService;
import com.portfolio.services.PortfolioViewService;
import com.portfolio.services.ProfileMasterService;
import com.portfolio.services.ResumePublicService;
import com.portfolio.services.ContactUsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final ContactUsService contactUsService;
    private final ProfileMasterService profileMasterService;
    private final ResumePublicService resumePublicService;
    private final PortfolioViewService portfolioViewService;
    private final FileService fileService;
    private final BlogPostService blogPostService;

    // Platform-level resource ID used for singleton assets (e.g. landing page banner)
    private static final long PLATFORM_RESOURCE_ID = 1L;

    @Operation(summary = "Get platform singleton file", description = "Returns the primary file asset for a platform-level resource type (e.g. BANNER). No auth required.")
    @GetMapping("/files/{resourceType}/singleton")
    public ResponseEntity<ResponseModel<FileAssetDTO>> getPlatformSingleton(@PathVariable String resourceType) {
        ResourceTypeEnum type = ResourceTypeEnum.valueOf(resourceType.toUpperCase());
        FileAssetDTO result = fileService.getPrimaryFile(PLATFORM_RESOURCE_ID, type);
        return ApiResponse.successResponse(result, "File fetched successfully");
    }

    @Operation(summary = "View resume", description = "Streams the active resume PDF for the given username directly in the browser.")
    @GetMapping("/resume/view/{username}")
    public void viewResume(
            @PathVariable String username,
            HttpServletResponse response
    ) throws GenericException {
        resumePublicService.viewResume(username, response);
    }

    @Operation(summary = "Download resume", description = "Downloads the active resume PDF for the given username as a file attachment.")
    @GetMapping("/resume/download/{username}")
    public void downloadResume(
            @PathVariable String username,
            HttpServletResponse response
    ) throws GenericException {
        resumePublicService.downloadResume(username, response);
    }

    @Operation(summary = "Get profile master data", description = "Returns the complete public portfolio data (profile, experience, education, skills, projects, etc.) for the domain in the Referer header.")
    @GetMapping("/profile-master")
    public ResponseEntity<ResponseModel<ProfileMasterResponse>> getProfileMasterByDomain(HttpServletRequest request) throws GenericException {
        String domain = request.getHeader("Referer");
        ProfileMasterResponse response = profileMasterService.getProfileMasterData(domain);
        return ApiResponse.respond( response, "Profile details fetched successfully", "Failed to fetch profile details"
        );
    }

    @Operation(summary = "Create contact message", description = "Saves a new contact us message.")
    @PostMapping("/contact-us")
    public ResponseEntity<ResponseModel<ContactUsResponse>> create(@Valid @RequestBody ContactUsRequest request) throws GenericException {
        ContactUsResponse response = contactUsService.create(request);
        return ApiResponse.respond(response, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Track portfolio view", description = "Records a page view from the public portfolio site.")
    @PostMapping("/track-view")
    public ResponseEntity<Void> trackView(@Valid @RequestBody PortfolioViewRequest request, HttpServletRequest httpRequest) {
        String ip = resolveClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        portfolioViewService.trackView(request, ip, ua);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get published blog posts", description = "Returns paginated published posts for a given username.")
    @GetMapping("/blog/{username}")
    public ResponseEntity<ResponseModel<Page<BlogPostSummary>>> getPublishedPosts(
            @PathVariable String username,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "publishedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Pageable pageable) throws GenericException {
        Page<BlogPostSummary> posts = blogPostService.getPublishedByUsername(username, search, sortBy, sortDir, pageable);
        return ApiResponse.successResponse(posts, "Blog posts fetched successfully");
    }

    @Operation(summary = "Get a single published blog post", description = "Returns post detail and increments view count.")
    @GetMapping("/blog/{username}/{slug}")
    public ResponseEntity<ResponseModel<BlogPostResponse>> getPublishedPost(
            @PathVariable String username,
            @PathVariable String slug) throws GenericException {
        BlogPostResponse post = blogPostService.getPublishedByUsernameAndSlug(username, slug);
        return ApiResponse.successResponse(post, "Blog post fetched successfully");
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
