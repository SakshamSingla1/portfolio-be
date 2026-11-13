package com.portfolio.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.Blog.BlogPostResponse;
import com.portfolio.dtos.Blog.BlogPostSummary;
import com.portfolio.dtos.Blog.BlogTagResponse;
import com.portfolio.dtos.ContactUs.ContactMessageResponse;
import com.portfolio.dtos.ContactUs.ContactThreadReplyRequest;
import com.portfolio.dtos.ContactUs.ContactThreadResponse;
import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewRequest;
import com.portfolio.dtos.Discover.DiscoverProfileResponse;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.Guestbook.GuestbookEntryRequest;
import com.portfolio.dtos.Guestbook.GuestbookEntryResponse;
import com.portfolio.dtos.Like.LikeStatusResponse;
import com.portfolio.dtos.Profile.ProfileAccessTokenResponse;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.dtos.Profile.VerifyProfileAccessRequest;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.dtos.SubscriptionPlan.SubscriptionPlanPublicResponse;
import com.portfolio.dtos.TestimonialLink.TestimonialLinkPublicResponse;
import com.portfolio.dtos.TestimonialLink.TestimonialSubmitRequest;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import lombok.extern.slf4j.Slf4j;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.BlogPostService;
import com.portfolio.services.FileService;
import com.portfolio.services.GuestbookService;
import com.portfolio.services.PortfolioExportService;
import com.portfolio.services.PortfolioLikeService;
import com.portfolio.services.PortfolioViewService;
import com.portfolio.services.ProfileAccessService;
import com.portfolio.services.ProfileMasterService;
import com.portfolio.services.ResumePublicService;
import com.portfolio.services.ContactUsService;
import com.portfolio.services.SocialLinkService;
import com.portfolio.services.SubscriptionPlanService;
import com.portfolio.services.TestimonialLinkService;
import com.portfolio.utils.CloudinaryUrlUtils;
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
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/public")
@RequiredArgsConstructor
@Slf4j
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
    private final ProfileAccessService profileAccessService;
    private final PortfolioLikeService portfolioLikeService;
    private final GuestbookService guestbookService;
    private final SocialLinkService socialLinkService;
    private final SubscriptionPlanService subscriptionPlanService;

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

    @Operation(summary = "Get profile master data", description = "Returns the complete public portfolio data (profile, experience, education, skills, projects, etc.) for the domain in the Referer header. 401s with PROFILE_ACCESS_REQUIRED if that profile is PRIVATE and no valid X-Profile-Access-Token is supplied.")
    @GetMapping("/profile-master")
    public ResponseEntity<ResponseModel<ProfileMasterResponse>> getProfileMasterByDomain(
            HttpServletRequest request,
            @RequestHeader(value = "X-Profile-Access-Token", required = false) String accessToken) throws GenericException {
        String domain = request.getHeader("Referer");
        // Always runs fresh, ahead of the cached fetch below -- see ProfileAccessServiceImpl's
        // javadoc for why this can't be folded into the cached method itself.
        profileAccessService.assertAccessAllowed(domain, accessToken);
        ProfileMasterResponse response = profileMasterService.getProfileMasterData(domain);
        // Matches the server-side Caffeine TTL for profileMasterByHost (CacheConfig) so the
        // browser cache never outlives what the backend itself considers fresh. Without this,
        // Spring Security's default Cache-Control: no-store forces a full network round trip
        // on every single page load/reload, even seconds apart.
        return ApiResponse.respond(response, "Profile details fetched successfully", "Failed to fetch profile details", 60);
    }

    @Operation(summary = "Verify portfolio access code", description = "Checks an access code against the PRIVATE profile that owns the domain in the Referer header and returns a token to replay via X-Profile-Access-Token.")
    @PostMapping("/profile-access/verify")
    public ResponseEntity<ResponseModel<ProfileAccessTokenResponse>> verifyProfileAccess(
            HttpServletRequest request,
            @Valid @RequestBody VerifyProfileAccessRequest body) throws GenericException {
        String domain = request.getHeader("Referer");
        String token = profileAccessService.verifyAccessCode(domain, body.getAccessCode());
        return ApiResponse.respond(new ProfileAccessTokenResponse(token), "Access granted", "Invalid access code");
    }

    @Operation(summary = "Create contact message", description = "Saves a new contact us message.")
    @PostMapping("/contact-us")
    public ResponseEntity<ResponseModel<ContactUsResponse>> create(@Valid @RequestBody ContactUsRequest request) throws GenericException {
        ContactUsResponse response = contactUsService.create(request);
        return ApiResponse.respond(response, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get conversation thread by token", description = "Lets the original visitor view the full conversation via the magic-link token emailed to them once the owner replies.")
    @GetMapping("/contact-thread/{token}")
    public ResponseEntity<ResponseModel<ContactThreadResponse>> getContactThread(@PathVariable String token) throws GenericException {
        return ApiResponse.respond(contactUsService.getThreadByToken(token),
                "Thread fetched successfully", "Failed to fetch thread");
    }

    @Operation(summary = "Reply in conversation thread", description = "Lets the original visitor reply in-app via their magic-link token instead of the conversation being a dead end.")
    @PostMapping("/contact-thread/{token}/reply")
    public ResponseEntity<ResponseModel<ContactMessageResponse>> replyToContactThread(
            @PathVariable String token,
            @Valid @RequestBody ContactThreadReplyRequest request) throws GenericException {
        return ApiResponse.respond(contactUsService.addVisitorReply(token, request.getMessage()),
                "Reply sent successfully", "Failed to send reply");
    }

    @Operation(summary = "Track portfolio view", description = "Records a page view from the public portfolio site.")
    @PostMapping("/track-view")
    public ResponseEntity<Void> trackView(@Valid @RequestBody PortfolioViewRequest request, HttpServletRequest httpRequest) {
        String ip = resolveClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        portfolioViewService.trackView(request, ip, ua);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get like status", description = "Returns the total like count for a profile and whether the given visitor has already liked it.")
    @GetMapping("/likes/{profileId}")
    public ResponseEntity<ResponseModel<LikeStatusResponse>> getLikeStatus(
            @PathVariable Long profileId,
            @RequestParam String visitorId) throws GenericException {
        return ApiResponse.respond(portfolioLikeService.getStatus(profileId, visitorId),
                "Like status fetched successfully", "Failed to fetch like status");
    }

    @Operation(summary = "Toggle like", description = "Likes the profile if the visitor hasn't already, otherwise unlikes it.")
    @PostMapping("/likes/{profileId}/toggle")
    public ResponseEntity<ResponseModel<LikeStatusResponse>> toggleLike(
            @PathVariable Long profileId,
            @RequestParam String visitorId) throws GenericException {
        return ApiResponse.respond(portfolioLikeService.toggleLike(profileId, visitorId),
                "Like updated successfully", "Failed to update like");
    }

    @Operation(summary = "Get guestbook entries", description = "Returns paginated, publicly-visible guestbook entries for a profile.")
    @GetMapping("/guestbook/{profileId}")
    public ResponseEntity<ResponseModel<Page<GuestbookEntryResponse>>> getGuestbookEntries(
            @PathVariable Long profileId, Pageable pageable) {
        return ApiResponse.respond(guestbookService.getPublicEntries(profileId, pageable),
                "Guestbook entries fetched successfully", "Failed to fetch guestbook entries");
    }

    @Operation(summary = "Add guestbook entry", description = "Leaves a public guestbook message on a profile's portfolio.")
    @PostMapping("/guestbook/{profileId}")
    public ResponseEntity<ResponseModel<GuestbookEntryResponse>> addGuestbookEntry(
            @PathVariable Long profileId,
            @Valid @RequestBody GuestbookEntryRequest request) {
        return ApiResponse.respond(guestbookService.addEntry(profileId, request),
                "Guestbook entry added successfully", "Failed to add guestbook entry");
    }

    @Operation(summary = "Get published blog posts", description = "Returns paginated published posts for a given username, optionally filtered by tag.")
    @GetMapping("/blog/{username}")
    public ResponseEntity<ResponseModel<Page<BlogPostSummary>>> getPublishedPosts(
            @PathVariable String username,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false, defaultValue = "publishedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Pageable pageable) throws GenericException {
        Page<BlogPostSummary> posts = blogPostService.getPublishedByUsername(username, search, tagId, sortBy, sortDir, pageable);
        return ApiResponse.respond(posts, "Blog posts fetched successfully", "Failed to fetch blog posts", 60);
    }

    @Operation(summary = "Get published blog tags", description = "Returns the distinct tags used across a given username's published posts.")
    @GetMapping("/blog/{username}/tags")
    public ResponseEntity<ResponseModel<List<BlogTagResponse>>> getPublishedBlogTags(
            @PathVariable String username) throws GenericException {
        List<BlogTagResponse> tags = blogPostService.getPublishedTagsByUsername(username);
        return ApiResponse.respond(tags, "Blog tags fetched successfully", "Failed to fetch blog tags", 60);
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
        Profile profile = profileDao.findByUserName(username)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        String portfolioUrl = resolvePortfolioUrl(profile.getId(), username);
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

    private String resolvePortfolioUrl(Long profileId, String username) {
        return socialLinkService.getByProfile(profileId).stream()
                .filter(l -> PlatformEnum.PORTFOLIO.equals(l.getPlatform()) && StatusEnum.ACTIVE.equals(l.getStatus()))
                .map(SocialLinkResponseDTO::getUrl)
                .filter(u -> u != null && !u.isBlank())
                .findFirst()
                .orElse(portfolioPublicBaseUrl + "/" + username);
    }

    @Operation(summary = "Get public subscription plans", description = "Returns active subscription plans (name, pricing, included highlights) for the landing page's pricing section, ordered for display. No auth required.")
    @GetMapping("/subscription-plans")
    public ResponseEntity<ResponseModel<List<SubscriptionPlanPublicResponse>>> getPublicSubscriptionPlans() {
        List<SubscriptionPlanPublicResponse> plans = subscriptionPlanService.getActivePlansPublic();
        return ApiResponse.respond(plans, "Subscription plans fetched successfully", "Failed to fetch subscription plans", 60);
    }

    @Operation(summary = "Explore discoverable portfolios", description = "Returns all publicly discoverable profiles, optionally filtered by a search term or skill.")
    @GetMapping("/explore")
    public ResponseEntity<ResponseModel<List<DiscoverProfileResponse>>> explore(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String skill) {
        List<DiscoverProfileResponse> results = profileDao.findDiscoverableProfiles(search, skill);
        return ApiResponse.respond(results, "Profiles fetched successfully", "Failed to fetch profiles", 60);
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

    @Operation(summary = "Export portfolio as PDF", description = "Generates the portfolio PDF and redirects to it on Cloudinary's CDN, forcing a download with the correct filename.")
    @GetMapping("/portfolio-export/{username}")
    public void exportPortfolio(@PathVariable String username, HttpServletResponse response) throws IOException {
        try {
            String url = portfolioExportService.exportPdf(username);
            response.sendRedirect(CloudinaryUrlUtils.withAttachmentFlag(url, "portfolio-" + username + ".pdf"));
        } catch (GenericException e) {
            // SERVICE_BUSY means another export is already rendering (see the export semaphore in
            // PortfolioExportServiceImpl) -- that's a "retry shortly", not a 404, and collapsing it
            // into NOT_FOUND is exactly what made repeated download clicks look like the resume/
            // profile didn't exist instead of telling the caller to back off.
            if (e.getExceptionCode() == ExceptionCodeEnum.SERVICE_BUSY) {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getErrorMessage());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Unexpected failure exporting portfolio PDF for username={}", username, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank() && isTrustedProxy(remoteAddr)) {
            return forwardedFor.split(",")[0].trim();
        }
        return remoteAddr;
    }

    private boolean isTrustedProxy(String ip) {
        return ip != null && (ip.startsWith("10.") || ip.startsWith("172.")
                || ip.startsWith("192.168.") || ip.equals("127.0.0.1")
                || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1"));
    }

}
