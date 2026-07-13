package com.portfolio.dtos.Profile;

import lombok.Data;

@Data
public class ProfileSettingsRequest {
    private Boolean isDiscoverable;
    private Boolean digestEmailEnabled;
}
