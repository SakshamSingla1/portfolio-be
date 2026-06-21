package com.portfolio.dtos.SocialLinks;

import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SocialLinkResponseDTO {
    private Long id;
    private PlatformEnum platform;
    private String url;
    private String order;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
