package com.portfolio.controllers;

import com.portfolio.dtos.Notification.NotificationResponseDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.NotificationService;
import com.portfolio.utils.Helper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final Helper helper;

    @Operation(summary = "Get notifications", description = "Returns a paginated list of notifications for the authenticated user's profile, newest first.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<NotificationResponseDTO>>> getAll(
            @RequestHeader(value = "Authorization", required = false) String auth,
            Pageable pageable
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        Page<NotificationResponseDTO> page = notificationService.getByProfile(profileId, pageable);
        return ApiResponse.successResponse(page, "Notifications fetched successfully");
    }

    @Operation(summary = "Get unread notification count", description = "Returns the number of unread notifications for the authenticated user's profile.")
    @GetMapping("/unread-count")
    public ResponseEntity<ResponseModel<Long>> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String auth
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.successResponse(notificationService.getUnreadCount(profileId), "Unread count fetched successfully");
    }

    @Operation(summary = "Mark notification as read", description = "Marks a single notification as read for the authenticated user's profile.")
    @PutMapping("/{id}/read")
    public ResponseEntity<ResponseModel<Void>> markAsRead(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long id
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        notificationService.markAsRead(id, profileId);
        return ApiResponse.successResponse(null, "Notification marked as read");
    }

    @Operation(summary = "Mark all notifications as read", description = "Marks all notifications as read for the authenticated user's profile.")
    @PutMapping("/read-all")
    public ResponseEntity<ResponseModel<Void>> markAllAsRead(
            @RequestHeader(value = "Authorization", required = false) String auth
    ) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(auth);
        notificationService.markAllAsRead(profileId);
        return ApiResponse.successResponse(null, "All notifications marked as read");
    }
}
