package com.portfolio.controllers;

import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewRequest;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.PortfolioViewService;
import com.portfolio.services.ProfileMasterService;
import com.portfolio.services.ResumePublicService;
import com.portfolio.servicesImpl.ContactUsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ResponseModel<ContactUsResponse>> create(@RequestBody ContactUsRequest request) throws GenericException {
        ContactUsResponse response = contactUsService.create(request);
        return ApiResponse.respond(response, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Track portfolio view", description = "Records a page view from the public portfolio site.")
    @PostMapping("/track-view")
    public ResponseEntity<Void> trackView(@RequestBody PortfolioViewRequest request, HttpServletRequest httpRequest) {
        String ip = resolveClientIp(httpRequest);
        String ua = httpRequest.getHeader("User-Agent");
        portfolioViewService.trackView(request, ip, ua);
        return ResponseEntity.ok().build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
