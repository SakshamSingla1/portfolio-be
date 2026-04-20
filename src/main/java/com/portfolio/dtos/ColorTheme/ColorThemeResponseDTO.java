package com.portfolio.dtos.ColorTheme;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ColorThemeResponseDTO extends AuditableResponse {
    private String id;
    private String themeName;
    private ColorPaletteDTO palette;
    private StatusEnum status;
}
