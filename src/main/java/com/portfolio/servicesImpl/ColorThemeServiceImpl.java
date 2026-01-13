package com.portfolio.servicesImpl;

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
import com.portfolio.repositories.ColorThemeRepository;
import com.portfolio.services.ColorThemeService;
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

    private final ColorThemeRepository repository;

    @Override
    public ColorThemeResponseDTO createTheme(ColorThemeRequestDTO dto) throws GenericException {

        String themeName = dto.getThemeName();

        if (repository.findByThemeName(themeName).isPresent()) {
            throw new GenericException(ExceptionCodeEnum.COLOR_THEME_ALREADY_EXISTS,
                    "Theme already exists for themeName '" + themeName + "'");
        }

        ColorTheme theme = ColorTheme.builder()
                .themeName(themeName)
                .palette(mapPaletteDtoToEntity(dto.getPalette()))
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .updatedBy("SUPER_ADMIN")
                .build();

        repository.save(theme);

        return mapToResponse(theme);
    }

    @Override
    public ColorThemeResponseDTO updateTheme(String id, ColorThemeRequestDTO dto) throws GenericException {

        ColorTheme theme = repository.findById(id)
                .orElseThrow(() ->
                        new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found"));

        theme.setThemeName(dto.getThemeName());
        theme.setPalette(mapPaletteDtoToEntity(dto.getPalette()));
        theme.setStatus(dto.getStatus());
        theme.setUpdatedAt(LocalDateTime.now());
        theme.setUpdatedBy("SUPER_ADMIN");
        repository.save(theme);
        return mapToResponse(theme);
    }

    @Override
    public ColorThemeResponseDTO getThemeByName(String themeName) throws GenericException {
        ColorTheme theme = repository.findByThemeName(themeName)
                .orElseThrow(() ->
                        new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND,
                                "Theme not found for theme " + themeName));
        return mapToResponse(theme);
    }

    @Override
    public Page<ColorThemeResponseDTO> getAllThemes(
            String search,
            String sortBy,
            String sortDir,
            StatusEnum status,
            Pageable pageable
    ) {

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt"
        );

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null;

        Page<ColorTheme> colorThemes;

        if (hasStatus) {
            colorThemes = repository.findByStatus(
                    status, sortedPageable
            );
        } else if (hasSearch) {
            colorThemes = repository.searchByThemeName(
                    search, sortedPageable
            );
        } else if(hasSearch && hasStatus ) {
            colorThemes = repository.searchByThemeNameAndStatus(search,status,sortedPageable);
        }else {
            colorThemes = repository.findAll(sortedPageable);
        }

        return colorThemes.map(this::mapToResponse);
    }

    @Override
    public ColorThemeResponseDTO getDefaultTheme() throws GenericException {
        return repository.findFirstByStatusOrderByCreatedAtDesc(StatusEnum.ACTIVE)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "No active default theme found"));
    }

    @Override
    public String deleteTheme(String id) throws GenericException {
        ColorTheme theme = repository.findById(id)
                .orElseThrow(() ->
                        new GenericException(ExceptionCodeEnum.COLOR_THEME_NOT_FOUND, "Theme not found"));
        repository.delete(theme);
        return "Theme deleted successfully";
    }

    private ColorPalette mapPaletteDtoToEntity(ColorPaletteDTO dto) {
        if (dto == null) return null;

        ColorPalette palette = new ColorPalette();
        List<ColorGroup> groups = dto.getColorGroups() == null ? List.of() :
                dto.getColorGroups().stream().map(this::mapGroupDtoToEntity).collect(Collectors.toList());
        palette.setColorGroups(groups);
        return palette;
    }

    private ColorGroup mapGroupDtoToEntity(ColorGroupDTO dto) {
        if (dto == null) return null;
        ColorGroup group = new ColorGroup();
        group.setGroupName(dto.getGroupName());
        List<ColorShade> levels = dto.getColorShades() == null ? List.of() :
                dto.getColorShades().stream().map(this::mapLevelDtoToEntity).collect(Collectors.toList());
        group.setColorShades(levels);
        return group;
    }

    private ColorShade mapLevelDtoToEntity(ColorShadeDTO dto) {
        if (dto == null) return null;
        ColorShade level = new ColorShade();
        level.setColorName(dto.getColorName());
        level.setColorCode(dto.getColorCode());
        return level;
    }

    private ColorThemeResponseDTO mapToResponse(ColorTheme theme) {
        ColorPaletteDTO paletteDTO = new ColorPaletteDTO();
        paletteDTO.setColorGroups(
                theme.getPalette() == null ? List.of() :
                        theme.getPalette().getColorGroups()
                                .stream()
                                .map(this::mapGroupEntityToDto)
                                .collect(Collectors.toList())
        );

        return ColorThemeResponseDTO.builder()
                .id(theme.getId())
                .themeName(theme.getThemeName())
                .palette(paletteDTO)
                .status(theme.getStatus())
                .createdAt(theme.getCreatedAt())
                .updatedAt(theme.getUpdatedAt())
                .updatedBy(theme.getUpdatedBy())
                .build();
    }

    private ColorGroupDTO mapGroupEntityToDto(ColorGroup group) {
        ColorGroupDTO dto = new ColorGroupDTO();
        dto.setGroupName(group.getGroupName());
        dto.setColorShades(
                group.getColorShades() == null ? List.of() :
                        group.getColorShades()
                                .stream()
                                .map(this::mapLevelEntityToDto)
                                .collect(Collectors.toList())
        );
        return dto;
    }

    private ColorShadeDTO mapLevelEntityToDto(ColorShade level) {
        ColorShadeDTO dto = new ColorShadeDTO();
        dto.setColorName(level.getColorName());
        dto.setColorCode(level.getColorCode());
        return dto;
    }
}
