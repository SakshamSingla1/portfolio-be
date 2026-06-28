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
@Table(name = "notification_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String messageTo;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String messageBody;

    private String emailTo;
    private String emailCc;
    private String emailBcc;
    private String emailReplyTo;
    private String template;

    private Integer isSms;
    private Integer isEmail;
    private Integer isWhatsapp;

    private String whatsappTemplateName;

    @Column(columnDefinition = "TEXT")
    private String whatsappTemplateBody;

    private String additionalData;
    private String dltTemplateId;
    private Long templateGroupId;
}
