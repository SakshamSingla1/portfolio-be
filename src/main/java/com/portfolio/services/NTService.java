package com.portfolio.services;

import com.portfolio.dtos.*;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface NTService {
    NTResponseDTO createNT(NTRequestDTO ntRequestDTO) throws GenericException;
    NTResponseDTO updateNT(String name,NTRequestDTO ntRequestDTO) throws GenericException;
    String deleteNT(String name) throws GenericException;
    NTResponseDTO findNTBy(String name) throws GenericException;
    Page<NTResponseDTO> getAllNotificationTemplates(Pageable pageable, String search, StatusEnum status, String sortBy, String sortDir);
    void sendNotification(String templateName, Map<String, Object> variables, String toEmail) throws GenericException;
}
