package com.portfolio.services;

import com.portfolio.dtos.Publication.PublicationRequestDTO;
import com.portfolio.dtos.Publication.PublicationResponseDTO;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PublicationService {
    PublicationResponseDTO create(PublicationRequestDTO dto) throws GenericException;
    PublicationResponseDTO update(Long id, PublicationRequestDTO dto) throws GenericException;
    PublicationResponseDTO getById(Long id) throws GenericException;
    Page<PublicationResponseDTO> getAll(Long profileId, String search, Pageable pageable);
    Void deleteById(Long id) throws GenericException;
    List<PublicationResponseDTO> getByProfile(Long profileId);
}
