package com.portfolio.controllers;

import com.portfolio.dtos.ContactUsRequest;
import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.dtos.ProfileMasterResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
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

    @GetMapping("/resume/view/{username}")
    public void viewResume(
            @PathVariable String username,
            HttpServletResponse response
    ) throws GenericException {
        resumePublicService.viewResume(username, response);
    }

    @GetMapping("/resume/download/{username}")
    public void downloadResume(
            @PathVariable String username,
            HttpServletResponse response
    ) throws GenericException {
        resumePublicService.downloadResume(username, response);
    }

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
}
