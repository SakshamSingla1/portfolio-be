package com.portfolio.dtos.SocialLinks;

import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocialLinkRequestDTO {
    private String profileId;
    private PlatformEnum platform;
    private String url;
    private String order;
    private StatusEnum status;
}
