package com.portfolio.dtos.Profile;

import com.portfolio.enums.ProfileVisibilityEnum;
import lombok.Data;

@Data
public class ProfileSettingsRequest {
    private Boolean isDiscoverable;
    private Boolean digestEmailEnabled;
    private ProfileVisibilityEnum visibility;
}
