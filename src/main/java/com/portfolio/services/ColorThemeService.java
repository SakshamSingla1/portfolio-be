package com.portfolio.services;

import com.portfolio.dtos.ColorTheme.ColorThemeRequestDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.enums.*;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColorThemeService {

    ColorThemeResponseDTO createTheme(ColorThemeRequestDTO dto) throws GenericException;

    ColorThemeResponseDTO updateTheme(Long id, ColorThemeRequestDTO dto) throws GenericException;

    ColorThemeResponseDTO getThemeById(Long themeId) throws GenericException;

    Page<ColorThemeResponseDTO> getAllThemes(
            String search,
            StatusEnum status,
            Pageable pageable);

    ColorThemeResponseDTO getDefaultTheme() throws GenericException;

    String deleteTheme(Long id) throws GenericException;
}
