package com.portfolio.dtos.SocialLinks;

import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocialLinkRequestDTO {
    private Long profileId;
    @NotNull(message = "Platform is required")
    private PlatformEnum platform;
    @NotBlank(message = "URL is required")
    private String url;
    private String order;
    private StatusEnum status;
}
