package com.portfolio.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.Blog.BlogPostResponse;
import com.portfolio.dtos.Blog.BlogPostSummary;
import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewRequest;
import com.portfolio.dtos.Discover.DiscoverProfileResponse;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.dtos.TestimonialLink.TestimonialLinkPublicResponse;
import com.portfolio.dtos.TestimonialLink.TestimonialSubmitRequest;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.BlogPostService;
import com.portfolio.services.FileService;
import com.portfolio.services.PortfolioExportService;
import com.portfolio.services.PortfolioViewService;
import com.portfolio.services.ProfileMasterService;
import com.portfolio.services.ResumePublicService;
import com.portfolio.services.ContactUsService;
import com.portfolio.services.TestimonialLinkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

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
    private final ProfileDao profileDao;
    private final TestimonialLinkService testimonialLinkService;
    private final PortfolioExportService portfolioExportService;

    @Value("${portfolio.public.base-url:http://localhost:5173}")
    private String portfolioPublicBaseUrl;

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

    @Operation(summary = "Download QR code", description = "Generates and returns a downloadable PNG QR code for the portfolio URL of the given username.")
    @GetMapping("/qr/{username}")
    public ResponseEntity<byte[]> getQrCode(@PathVariable String username) throws Exception {
        profileDao.findByUserName(username)
                .orElseThrow(() -> new GenericException(null, "Profile not found"));

        String portfolioUrl = portfolioPublicBaseUrl + "/" + username;
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = Map.of(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = writer.encode(portfolioUrl, BarcodeFormat.QR_CODE, 400, 400, hints);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"portfolio-qr-" + username + ".png\"")
                .body(out.toByteArray());
    }

    @Operation(summary = "Explore discoverable portfolios", description = "Returns all publicly discoverable profiles, optionally filtered by a search term or skill.")
    @GetMapping("/explore")
    public ResponseEntity<ResponseModel<List<DiscoverProfileResponse>>> explore(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String skill) {
        List<DiscoverProfileResponse> results = profileDao.findDiscoverableProfiles(search, skill);
        return ApiResponse.successResponse(results, "Profiles fetched successfully");
    }

    @Operation(summary = "Get public testimonial link details", description = "Returns owner name and optional requester name for the testimonial form. No auth required.")
    @GetMapping("/testimonial-requests/{token}")
    public ResponseEntity<ResponseModel<TestimonialLinkPublicResponse>> getPublicLinkDetails(
            @PathVariable String token) throws GenericException {
        TestimonialLinkPublicResponse response = testimonialLinkService.getPublicLinkDetails(token);
        return ApiResponse.successResponse(response, "Testimonial link details fetched successfully");
    }

    @Operation(summary = "Submit a testimonial via share link", description = "Saves a new testimonial (status=PENDING) and marks the link as used. No auth required.")
    @PostMapping("/testimonial-requests/{token}/submit")
    public ResponseEntity<Void> submitTestimonial(
            @PathVariable String token,
            @Valid @RequestBody TestimonialSubmitRequest req) throws GenericException {
        testimonialLinkService.submitTestimonial(token, req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Export portfolio as PDF", description = "Generates and returns a downloadable PDF of the full portfolio for the given username.")
    @GetMapping("/portfolio-export/{username}")
    public ResponseEntity<byte[]> exportPortfolio(@PathVariable String username) {
        try {
            byte[] pdf = portfolioExportService.exportPdf(username);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "portfolio-" + username + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
