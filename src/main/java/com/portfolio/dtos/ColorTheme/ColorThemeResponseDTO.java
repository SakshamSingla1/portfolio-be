package com.portfolio.dtos.ColorTheme;

import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ColorThemeResponseDTO {
    private String id;
    private String themeName;
    private ColorPaletteDTO palette;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
