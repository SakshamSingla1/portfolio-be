package com.portfolio.dtos.TestimonialLink;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TestimonialLinkResponse {

    private Long id;
    private Long profileId;
    private String requesterName;
    private String requesterEmail;
    private String token;
    private String shareUrl;
    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;
}
