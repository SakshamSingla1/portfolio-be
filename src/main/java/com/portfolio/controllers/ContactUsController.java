package com.portfolio.controllers;

import com.portfolio.dtos.ContactUsRequest;
import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ContactUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling Contact Us requests.
 * Supports creating a new contact message and fetching messages by profile with pagination.
 */
@RestController
@RequestMapping("/api/v1/contact-us")
public class ContactUsController {

    @Autowired
    private ContactUsService contactUsService;

    // 🔹 CREATE CONTACT MESSAGE
    @PostMapping
    public ResponseEntity<ResponseModel<ContactUsResponse>> create(@RequestBody ContactUsRequest request) throws GenericException {
        ContactUsResponse response = contactUsService.create(request);
        return ApiResponse.respond(response, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    // 🔹 GET CONTACTS BY PROFILE WITH PAGINATION
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ResponseModel<Page<ContactUsResponse>>> getByProfile(
            @PathVariable Integer profileId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) throws GenericException {
        Page<ContactUsResponse> page = contactUsService.getContactUsByProfileId(profileId, pageable, search);
        return ApiResponse.respond(page, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }
}
