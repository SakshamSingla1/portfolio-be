package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "notification_template_variables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateVariables extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variable_name")
    private String variableName;

    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;

    private Long whatsappVariable;
    private Long templateId;
}
