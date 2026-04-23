package com.portfolio.dtos.ProfileTheme;

import com.portfolio.dtos.AuditableResponse;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.Data;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProfileThemeResponse extends AuditableResponse {

    private String id;

    private String profileId;

    private String username;

    private String themeId;

    private String themeName;
}