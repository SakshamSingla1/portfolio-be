package com.portfolio.dtos.Certifications;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CertificationResponseDTO extends AuditableResponse {
    private String id;
    private String title;
    private String issuer;
    private String credentialId;
    private String credentialUrl;
    private StatusEnum status;
    private String order;
    private LocalDate issueDate;
    private LocalDate expiryDate;
}
