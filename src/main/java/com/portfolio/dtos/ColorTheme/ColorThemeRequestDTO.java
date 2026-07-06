package com.portfolio.dtos.ColorTheme;

import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ColorThemeRequestDTO {
    @NotBlank(message = "Theme name is required")
    private String themeName;
    private ColorPaletteDTO palette;
    private StatusEnum status;
}
