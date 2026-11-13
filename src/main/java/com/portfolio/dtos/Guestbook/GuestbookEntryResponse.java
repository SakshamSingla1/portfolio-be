package com.portfolio.dtos.Guestbook;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GuestbookEntryResponse {

    private Long id;
    private String visitorName;
    private String message;
    private LocalDateTime createdAt;
    // Only populated for the owner's own management view (PublicController's visitor-facing
    // list never includes it) -- lets the dashboard show hidden entries inline instead of
    // filtering them out of the page entirely.
    private String status;
}
