package com.portfolio.services;

import com.portfolio.dtos.*;
import com.portfolio.dtos.ColorTheme.ColorThemeRequestDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.enums.*;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ColorThemeService {

    ColorThemeResponseDTO createTheme(ColorThemeRequestDTO dto) throws GenericException;

    ColorThemeResponseDTO updateTheme(String id, ColorThemeRequestDTO dto) throws GenericException;

    ColorThemeResponseDTO getThemeByName(String themeName) throws GenericException;

    // ---------------------------
    // GET ALL THEMES BY ROLE
    // ---------------------------
    Page<ColorThemeResponseDTO> getAllThemes(
            String search,
            String sortBy,
            String sortDir,
            StatusEnum status,
            Pageable pageable
    );

    ColorThemeResponseDTO getDefaultTheme() throws GenericException;

    String deleteTheme(String id) throws GenericException;
}
