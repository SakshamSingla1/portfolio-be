package com.portfolio.services;

import com.portfolio.dtos.NotificationTemplates.NTRequestDTO;
import com.portfolio.dtos.NotificationTemplates.NTResponseDTO;
import com.portfolio.dtos.NotificationTemplates.NotificationTemplateListResponseDTO;
import com.portfolio.dtos.NotificationTemplates.NotificationTemplateVariablesListResponseDTO;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface NTService {

    NotificationTemplate createNT(NTRequestDTO requestDTO) throws GenericException;

    NotificationTemplate updateNT(Long id, NTRequestDTO requestDTO) throws GenericException;

    NTResponseDTO findNTById(Long id) throws GenericException;

    Page<NotificationTemplateListResponseDTO> getAllByCriteria(String search, String templateGroupIdString, Pageable pageable);

    Page<NotificationTemplateVariablesListResponseDTO> getVariablesByCriteria(String search, Pageable pageable);

    void sendNotification(String templateName, Map<String, Object> variables, String toEmail) throws GenericException;
}
