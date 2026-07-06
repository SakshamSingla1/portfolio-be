package com.portfolio.dtos.SocialLinks;

import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLinkResponseDTO {
    private Long id;
    private PlatformEnum platform;
    private String url;
    private Integer order;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
