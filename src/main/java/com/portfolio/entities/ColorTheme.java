package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.converters.ColorPaletteConverter;
import com.portfolio.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "color_themes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorTheme extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String themeName;

    @Convert(converter = ColorPaletteConverter.class)
    @Column(columnDefinition = "TEXT")
    private ColorPalette palette;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
