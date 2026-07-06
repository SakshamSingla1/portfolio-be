package com.portfolio.dtos.ProfileTheme;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileThemeRequest {
    @NotNull(message = "Theme ID is required")
    private Long themeId;
}
