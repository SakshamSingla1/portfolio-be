package com.portfolio.dtos.Platform;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformSettingsDTO {
    private String bannerImageUrl;
    private String bannerAssetId;
}
