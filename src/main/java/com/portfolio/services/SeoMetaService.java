package com.portfolio.services;

import com.portfolio.dtos.SeoMeta.SeoMetaRequestDTO;
import com.portfolio.dtos.SeoMeta.SeoMetaResponseDTO;
import com.portfolio.enums.PageKeyEnum;
import com.portfolio.exceptions.GenericException;

import java.util.List;

public interface SeoMetaService {
    List<SeoMetaResponseDTO> getAllByProfile(String authHeader) throws GenericException;
    SeoMetaResponseDTO getByPageKey(String authHeader, PageKeyEnum pageKey) throws GenericException;
    SeoMetaResponseDTO upsert(String authHeader, SeoMetaRequestDTO dto) throws GenericException;
}
