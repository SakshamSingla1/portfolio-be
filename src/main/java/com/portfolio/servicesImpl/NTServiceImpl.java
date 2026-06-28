package com.portfolio.servicesImpl;

import com.portfolio.dtos.NotificationTemplates.NTRequestDTO;
import com.portfolio.dtos.NotificationTemplates.NTResponseDTO;
import com.portfolio.dtos.NotificationTemplates.NotificationTemplateListResponseDTO;
import com.portfolio.dtos.NotificationTemplates.NotificationTemplateVariablesListResponseDTO;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.NTRepository;
import com.portfolio.repositories.NTVariablesRepository;
import com.portfolio.services.EmailService;
import com.portfolio.services.NTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NTServiceImpl implements NTService {

    private final NTRepository ntRepository;
    private final NTVariablesRepository ntVariablesRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public NotificationTemplate createNT(NTRequestDTO dto) throws GenericException {
        NotificationTemplate template = NotificationTemplate.builder()
                .message(dto.getMessage())
                .messageTo(dto.getMessageTo())
                .subject(dto.getSubject())
                .messageBody(dto.getMessageBody())
                .emailTo(dto.getEmailTo())
                .emailCc(dto.getEmailCc())
                .emailBcc(dto.getEmailBcc())
                .emailReplyTo(dto.getEmailReplyTo())
                .template(dto.getTemplate())
                .isSms(dto.getIsSms())
                .isEmail(dto.getIsEmail())
                .isWhatsapp(dto.getIsWhatsapp())
                .whatsappTemplateName(dto.getWhatsappTemplateName())
                .whatsappTemplateBody(dto.getWhatsappTemplateBody())
                .additionalData(dto.getAdditionalData())
                .dltTemplateId(dto.getDltTemplateId())
                .templateGroupId(dto.getTemplateGroupId())
                .build();
        return ntRepository.save(template);
    }

    @Override
    @Transactional
    public NotificationTemplate updateNT(Long id, NTRequestDTO dto) throws GenericException {
        NotificationTemplate existing = ntRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND, "Notification template not found"));

        existing.setMessage(dto.getMessage());
        existing.setMessageTo(dto.getMessageTo());
        existing.setSubject(dto.getSubject());
        existing.setMessageBody(dto.getMessageBody());
        existing.setEmailTo(dto.getEmailTo());
        existing.setEmailCc(dto.getEmailCc());
        existing.setEmailBcc(dto.getEmailBcc());
        existing.setEmailReplyTo(dto.getEmailReplyTo());
        existing.setTemplate(existing.getTemplate()); // template name is immutable
        existing.setIsSms(dto.getIsSms());
        existing.setIsEmail(dto.getIsEmail());
        existing.setIsWhatsapp(dto.getIsWhatsapp());
        existing.setWhatsappTemplateName(dto.getWhatsappTemplateName());
        existing.setWhatsappTemplateBody(dto.getWhatsappTemplateBody());
        existing.setAdditionalData(dto.getAdditionalData());
        existing.setDltTemplateId(dto.getDltTemplateId());
        existing.setTemplateGroupId(dto.getTemplateGroupId());
        return ntRepository.save(existing);
    }

    @Override
    public NTResponseDTO findNTById(Long id) throws GenericException {
        NotificationTemplate nt = ntRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND, "Notification template not found"));
        return mapToResponse(nt);
    }

    @Override
    public Page<NotificationTemplateListResponseDTO> getAllByCriteria(
            String search, String templateGroupIdString, Pageable pageable) {
        List<Long> groupIds = parseGroupIds(templateGroupIdString);
        String searchValue = (search == null || search.isBlank()) ? "" : search.trim();
        if (groupIds == null) {
            return ntRepository.findByCriteria(searchValue, pageable);
        }
        return ntRepository.findByCriteriaWithGroups(searchValue, groupIds, pageable);
    }

    @Override
    public Page<NotificationTemplateVariablesListResponseDTO> getVariablesByCriteria(String search, Pageable pageable) {
        String searchValue = (search == null || search.isBlank()) ? "" : search.trim();
        return ntVariablesRepository.findByCriteria(searchValue, pageable);
    }

    @Override
    public void sendNotification(String templateName, Map<String, Object> variables, String toEmail) throws GenericException {
        NotificationTemplate nt = ntRepository.findByTemplate(templateName)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND, "Notification template not found: " + templateName));
        String subject = nt.getSubject() != null ? nt.getSubject() : "";
        String body    = nt.getMessageBody() != null ? nt.getMessageBody() : "";
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            String placeholder = "{{" + entry.getKey() + "}}";
            subject = subject.replace(placeholder, value);
            body    = body.replace(placeholder, value);
        }
        emailService.sendEmail(toEmail, subject, body);
    }

    private List<Long> parseGroupIds(String templateGroupIdString) {
        if (templateGroupIdString == null || templateGroupIdString.isBlank()) return null;
        try {
            return Arrays.stream(templateGroupIdString.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private NTResponseDTO mapToResponse(NotificationTemplate nt) {
        return NTResponseDTO.builder()
                .id(nt.getId())
                .message(nt.getMessage())
                .messageTo(nt.getMessageTo())
                .subject(nt.getSubject())
                .messageBody(nt.getMessageBody())
                .emailTo(nt.getEmailTo())
                .emailCc(nt.getEmailCc())
                .emailBcc(nt.getEmailBcc())
                .emailReplyTo(nt.getEmailReplyTo())
                .template(nt.getTemplate())
                .isSms(nt.getIsSms())
                .isEmail(nt.getIsEmail())
                .isWhatsapp(nt.getIsWhatsapp())
                .whatsappTemplateName(nt.getWhatsappTemplateName())
                .whatsappTemplateBody(nt.getWhatsappTemplateBody())
                .additionalData(nt.getAdditionalData())
                .dltTemplateId(nt.getDltTemplateId())
                .templateGroupId(nt.getTemplateGroupId())
                .createdBy(nt.getCreatedBy())
                .updatedBy(nt.getUpdatedBy())
                .createdAt(nt.getCreatedAt())
                .updatedAt(nt.getUpdatedAt())
                .build();
    }
}
