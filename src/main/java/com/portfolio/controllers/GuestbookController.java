package com.portfolio.controllers;

import com.portfolio.dtos.Guestbook.GuestbookEntryResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.GuestbookService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/guestbook")
@PreAuthorize("isAuthenticated()")
public class GuestbookController {

    private final GuestbookService guestbookService;
    private final Helper helper;

    @Operation(summary = "Get guestbook entries", description = "Returns every guestbook entry (ACTIVE and hidden) for the authenticated user's own profile.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<GuestbookEntryResponse>>> getEntries(
            @RequestHeader(value = "Authorization", required = false) String auth,
            Pageable pageable) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(guestbookService.getForOwner(profileId, pageable),
                "Guestbook entries fetched successfully", "Failed to fetch guestbook entries");
    }

    @Operation(summary = "Hide guestbook entry", description = "Hides a guestbook entry from the public portfolio page without deleting it.")
    @PutMapping("/{id}/hide")
    public ResponseEntity<ResponseModel<String>> hide(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        guestbookService.setVisibility(profileId, id, false);
        return ApiResponse.successResponse(null, "Guestbook entry hidden");
    }

    @Operation(summary = "Unhide guestbook entry", description = "Makes a previously hidden guestbook entry publicly visible again.")
    @PutMapping("/{id}/unhide")
    public ResponseEntity<ResponseModel<String>> unhide(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        guestbookService.setVisibility(profileId, id, true);
        return ApiResponse.successResponse(null, "Guestbook entry unhidden");
    }

    @Operation(summary = "Delete guestbook entry", description = "Permanently deletes a guestbook entry.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        guestbookService.deleteEntry(profileId, id);
        return ApiResponse.successResponse(null, "Guestbook entry deleted");
    }
}
