package com.portfolio.controllers;

import com.portfolio.dtos.ContactUs.ContactUsReplyDTO;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.enums.ContactUsStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ContactUsService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact-us")
@RequiredArgsConstructor
@Tag(name = "Contact Us", description = "Endpoints for managing Contact Us messages")
@PreAuthorize("isAuthenticated()")
public class ContactUsController {

    private final ContactUsService contactUsService;
    private final Helper helper;

    @Operation(summary = "Get contact messages by profile", description = "Fetches paginated contact messages for a profile with optional search.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<ContactUsResponse>>> getByProfile(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ContactUsStatusEnum status,
            Pageable pageable
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<ContactUsResponse> page = contactUsService.getContactUsByProfileId(profileId, search, status, pageable);
        return ApiResponse.respond(page, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Marks as Read", description = "Make unread message to read")
    @PatchMapping("/{id}/mark-read")
    public ResponseEntity<ResponseModel<String>> updateStatus(
            @PathVariable Long id
    ) throws GenericException {
        contactUsService.updateStatus(id, ContactUsStatusEnum.READ);
        return ApiResponse.respond("Success", ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Reply to a contact message", description = "Sends an email reply to the sender and marks the message as REPLIED.")
    @PostMapping("/{id}/reply")
    public ResponseEntity<ResponseModel<ContactUsResponse>> reply(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody ContactUsReplyDTO dto
    ) throws GenericException {
        ContactUsResponse response = contactUsService.reply(id, dto.getMessage(), auth);
        return ApiResponse.respond(response, "Reply sent successfully", ApiResponse.FAILED);
    }

}
