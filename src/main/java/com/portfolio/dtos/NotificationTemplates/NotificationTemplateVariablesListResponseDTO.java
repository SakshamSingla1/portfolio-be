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
public class NotificationTemplateVariablesListResponseDTO {

    private Long id;
    private String variableName;
    private String htmlContent;
    private Long whatsappVariable;
    private Long templateId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
