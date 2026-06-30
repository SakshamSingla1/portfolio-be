package com.portfolio.dtos.ColorTheme;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ColorThemeResponseDTO extends AuditableResponse {
    private Long id;
    private String themeName;
    private ColorPaletteDTO palette;
    private StatusEnum status;

    public ColorThemeResponseDTO(Long id, String themeName, ColorPaletteDTO palette,
                                 StatusEnum status,LocalDateTime createdAt,
                                 LocalDateTime updatedAt,Long createdBy, Long updatedBy,
                                 String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.themeName = themeName;
        this.palette = palette;
        this.status = status;
    }
}
