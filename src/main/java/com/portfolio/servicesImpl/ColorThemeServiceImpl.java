package com.portfolio.servicesImpl;

import com.portfolio.dao.color_theme.ColorThemeDao;
import com.portfolio.dtos.ColorTheme.ColorGroupDTO;
import com.portfolio.dtos.ColorTheme.ColorShadeDTO;
import com.portfolio.dtos.ColorTheme.ColorPaletteDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeRequestDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.entities.ColorGroup;
import com.portfolio.entities.ColorShade;
import com.portfolio.entities.ColorPalette;
import com.portfolio.entities.ColorTheme;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ColorThemeService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorThemeServiceImpl implements ColorThemeService {

        private final ColorThemeDao colorThemeDao;
        private final Helper helper;

        @Override
        public ColorThemeResponseDTO createTheme(ColorThemeRequestDTO dto) throws GenericException {

                String themeName = dto.getThemeName();

                if (colorThemeDao.findByThemeName(themeName).isPresent()) {
                        throw new GenericException(ExceptionCodeEnum.COLOR_THEME_ALREADY_EXISTS,
                                        "Theme already exists for themeName '" + themeName + "'");
                }

                ColorTheme theme = ColorTheme.builder()
                                .themeName(themeName)
                                .palette(mapPaletteDtoToEntity(dto.getPalette()))
                                .status(StatusEnum.ACTIVE)
                                .build();

                colorThemeDao.save(theme);

                return mapToResponse(theme);
        }

        @Override
        public ColorThemeResponseDTO updateTheme(Long id, ColorThemeRequestDTO dto) throws GenericException {

                ColorTheme theme = colorThemeDao.findById(id)
                                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND,
                                                "Theme not found"));

                theme.setThemeName(dto.getThemeName());
                theme.setPalette(mapPaletteDtoToEntity(dto.getPalette()));
                theme.setStatus(dto.getStatus());
                theme.setUpdatedAt(LocalDateTime.now());
                colorThemeDao.save(theme);
                return mapToResponse(theme);
        }

        @Override
        public ColorThemeResponseDTO getThemeById(Long themeId) throws GenericException {
                ColorTheme theme = colorThemeDao.findById(themeId)
                                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND,
                                                "Theme not found for id " + themeId));
                return mapToResponse(theme);
        }

        @Override
        public Page<ColorThemeResponseDTO> getAllThemes(
                        String search,
                        StatusEnum status,
                        Pageable pageable) {
                return colorThemeDao.findByCriteria(search, status, pageable)
                        .map(this::mapToResponse);
        }

        @Override
        public ColorThemeResponseDTO getDefaultTheme() throws GenericException {
                return colorThemeDao.findFirstByStatusOrderByCreatedAtDesc(StatusEnum.ACTIVE)
                                .map(this::mapToResponse)
                                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND,
                                                "No active default theme found"));
        }

        @Override
        public String deleteTheme(Long id) throws GenericException {
                ColorTheme theme = colorThemeDao.findById(id)
                                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND,
                                                "Theme not found"));
                colorThemeDao.delete(theme);
                return "Theme deleted successfully";
        }

        private ColorPalette mapPaletteDtoToEntity(ColorPaletteDTO dto) {
                if (dto == null)
                        return null;

                ColorPalette palette = new ColorPalette();
                List<ColorGroup> groups = dto.getColorGroups() == null ? List.of()
                                : dto.getColorGroups().stream().map(this::mapGroupDtoToEntity)
                                                .collect(Collectors.toList());
                palette.setColorGroups(groups);
                return palette;
        }

        private ColorGroup mapGroupDtoToEntity(ColorGroupDTO dto) {
                if (dto == null)
                        return null;
                ColorGroup group = new ColorGroup();
                group.setGroupName(dto.getGroupName());
                List<ColorShade> levels = dto.getColorShades() == null ? List.of()
                                : dto.getColorShades().stream().map(this::mapLevelDtoToEntity)
                                                .collect(Collectors.toList());
                group.setColorShades(levels);
                return group;
        }

        private ColorShade mapLevelDtoToEntity(ColorShadeDTO dto) {
                if (dto == null)
                        return null;
                ColorShade level = new ColorShade();
                level.setColorName(dto.getColorName());
                level.setColorCode(dto.getColorCode());
                return level;
        }

        private ColorThemeResponseDTO mapToResponse(ColorTheme theme) {
                ColorPaletteDTO paletteDTO = new ColorPaletteDTO();
                paletteDTO.setColorGroups(
                                theme.getPalette() == null ? List.of()
                                                : theme.getPalette().getColorGroups()
                                                                .stream()
                                                                .map(this::mapGroupEntityToDto)
                                                                .collect(Collectors.toList()));

                ColorThemeResponseDTO responseDTO = ColorThemeResponseDTO.builder()
                                .id(theme.getId())
                                .themeName(theme.getThemeName())
                                .palette(paletteDTO)
                                .status(theme.getStatus())
                                .build();
                helper.setAudit(theme, responseDTO);
                return responseDTO;
        }

        private ColorGroupDTO mapGroupEntityToDto(ColorGroup group) {
                ColorGroupDTO dto = new ColorGroupDTO();
                dto.setGroupName(group.getGroupName());
                dto.setColorShades(
                                group.getColorShades() == null ? List.of()
                                                : group.getColorShades()
                                                                .stream()
                                                                .map(this::mapLevelEntityToDto)
                                                                .collect(Collectors.toList()));
                return dto;
        }

        private ColorShadeDTO mapLevelEntityToDto(ColorShade level) {
                ColorShadeDTO dto = new ColorShadeDTO();
                dto.setColorName(level.getColorName());
                dto.setColorCode(level.getColorCode());
                return dto;
        }
}
