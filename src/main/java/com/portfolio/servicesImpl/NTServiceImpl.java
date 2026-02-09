package com.portfolio.servicesImpl;

import com.portfolio.dtos.*;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.NTRepository;
import com.portfolio.services.EmailService;
import com.portfolio.services.NTService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NTServiceImpl implements NTService {

    private final NTRepository ntRepository;
    private final EmailService emailService;

    @Override
    public NTResponseDTO createNT(NTRequestDTO ntRequestDTO) throws GenericException {
        if (ntRepository.existsByNameAndType(ntRequestDTO.getName(), ntRequestDTO.getType())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_TEMPLATE,
                    "Template with the same name and type already exists");
        }
        NotificationTemplate template = NotificationTemplate.builder()
                .name(ntRequestDTO.getName())
                .subject(ntRequestDTO.getSubject())
                .body(ntRequestDTO.getBody())
                .type(ntRequestDTO.getType())
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ntRepository.save(template);
        return mapToResponseDTO(template);
    }

    @Override
    public NTResponseDTO updateNT(String name, NTRequestDTO ntRequestDTO) throws GenericException {
        NotificationTemplate existingTemplate = ntRepository.findByName(name)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND,
                        "Template not found for update"));

        existingTemplate.setSubject(ntRequestDTO.getSubject());
        existingTemplate.setBody(ntRequestDTO.getBody());
        existingTemplate.setStatus(ntRequestDTO.getStatus());
        existingTemplate.setUpdatedAt(LocalDateTime.now());

        ntRepository.save(existingTemplate);
        return mapToResponseDTO(existingTemplate);
    }

    @Override
    public String deleteNT(String name) throws GenericException {
        NotificationTemplate template = ntRepository.findByName(name)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND,
                        "Template not found for deletion"));

        ntRepository.delete(template);
        return "Template deleted successfully";
    }

    @Override
    public NTResponseDTO findNTBy(String name) throws GenericException {
        NotificationTemplate template = ntRepository.findByName(name)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND,
                        "Template not found"));
        return mapToResponseDTO(template);
    }

    @Override
    public Page<NTResponseDTO> getAllNotificationTemplates(
            Pageable pageable, String search, StatusEnum status, String sortBy, String sortDir) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasStatus = status != null;
        boolean hasSearch = search != null && !search.isBlank();

        Page<NotificationTemplate> notificationTemplates;
        if( hasSearch && hasStatus){
            notificationTemplates = ntRepository.searchByStatusAndSearch(search,status,sortedPageable);
        }else if(hasSearch){
            notificationTemplates = ntRepository.SearchByText(search,sortedPageable);
        }else if(hasStatus) {
            notificationTemplates = ntRepository.findByStatus(status, sortedPageable);
        }else{
            notificationTemplates = ntRepository.findAll(sortedPageable);
        }
        return notificationTemplates.map(this::mapToResponseDTO);
    }

    @Override
    public void sendNotification(String templateName, Map<String, Object> variables, String toEmail) throws GenericException {
        NotificationTemplate template = ntRepository.findByName(templateName)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TEMPLATE_NOT_FOUND, "Template not found: " + templateName));
        String subject = template.getSubject();
        String body = template.getBody();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            subject = subject.replace("{{" + entry.getKey() + "}}", value);
            body = body.replace("{{" + entry.getKey() + "}}", value);
        }
        emailService.sendEmail(toEmail, subject, body);
    }

    private NTResponseDTO mapToResponseDTO(NotificationTemplate template) {
        return NTResponseDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .subject(template.getSubject())
                .body(template.getBody())
                .type(template.getType())
                .status(template.getStatus())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
