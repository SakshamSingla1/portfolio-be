package com.portfolio.dtos.NotificationTemplates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateListResponseDTO {

    private Long id;
    private String subject;
    private String template;
    private Integer isSms;
    private Integer isEmail;
    private Integer isWhatsapp;
    private String whatsappTemplateName;
    private Long templateGroupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
