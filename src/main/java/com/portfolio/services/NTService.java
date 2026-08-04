package com.portfolio.services;

import com.portfolio.dtos.NotificationTemplates.NTRequestDTO;
import com.portfolio.dtos.NotificationTemplates.NTResponseDTO;
import com.portfolio.dtos.NotificationTemplates.NotificationTemplateListResponseDTO;
import com.portfolio.dtos.NotificationTemplates.NotificationTemplateVariablesListResponseDTO;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

public interface NTService {

    NotificationTemplate createNT(NTRequestDTO requestDTO) throws GenericException;

    NotificationTemplate updateNT(Long id, NTRequestDTO requestDTO) throws GenericException;

    void deleteNT(Long id) throws GenericException;

    NTResponseDTO findNTById(Long id) throws GenericException;

    Page<NotificationTemplateListResponseDTO> getAllByCriteria(String search, String templateGroupIdString, Pageable pageable);

    Page<NotificationTemplateVariablesListResponseDTO> getVariablesByCriteria(String search, Pageable pageable);

    /**
     * Sends the templated email on a background thread (see AsyncConfig). Callers must
     * persist any DB state (OTP, reset token, contact record) before calling this — a
     * template-lookup failure here is only logged, not propagated to the caller, since
     * the caller has typically already returned its response by the time this runs.
     */
    @Async("notificationExecutor")
    void sendNotification(String templateName, Map<String, Object> variables, String toEmail) throws GenericException;
}
