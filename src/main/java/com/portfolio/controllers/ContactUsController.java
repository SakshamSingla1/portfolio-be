package com.portfolio.controllers;

import com.portfolio.dtos.ContactUsRequest;
import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.servicesImpl.ContactUsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/contact-us")
@Tag(name = "Contact Us", description = "Endpoints for managing Contact Us messages")
public class ContactUsController {

    @Autowired
    private ContactUsService contactUsService;

    @Operation(summary = "Create contact message", description = "Saves a new contact us message.")
    @PostMapping
    public ResponseEntity<ResponseModel<ContactUsResponse>> create(@RequestBody ContactUsRequest request) throws GenericException {
        ContactUsResponse response = contactUsService.create(request);
        return ApiResponse.respond(response, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get contact messages by profile", description = "Fetches paginated contact messages for a profile with optional search.")
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseModel<Page<ContactUsResponse>>> getByProfile(
            @PathVariable String profileId,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) throws GenericException {
        Page<ContactUsResponse> page = contactUsService.getContactUsByProfileId(profileId, pageable, search,sortBy,sortDir);
        return ApiResponse.respond(page, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }
}
