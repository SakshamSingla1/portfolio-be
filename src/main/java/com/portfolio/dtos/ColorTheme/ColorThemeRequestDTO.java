package com.portfolio.dtos.ColorTheme;

import com.portfolio.enums.StatusEnum;
import lombok.Data;

@Data
public class ColorThemeRequestDTO {
    private String themeName;
    private ColorPaletteDTO palette;
    private StatusEnum status;
}
