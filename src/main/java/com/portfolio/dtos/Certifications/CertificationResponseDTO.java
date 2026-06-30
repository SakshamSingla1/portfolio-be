package com.portfolio.dtos.Certifications;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CertificationResponseDTO extends AuditableResponse {
    private Long id;
    private String title;
    private String issuer;
    private String credentialId;
    private String credentialUrl;
    private StatusEnum status;
    private String order;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    public CertificationResponseDTO(Long id, String title, String issuer,
                                    String credentialId, String credentialUrl,
                                    StatusEnum status, String order,
                                    LocalDate issueDate, LocalDate expiryDate,
                                    LocalDateTime createdAt, LocalDateTime updatedAt,
                                    Long createdBy, Long updatedBy,
                                    String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.title = title;
        this.issuer = issuer;
        this.credentialId = credentialId;
        this.credentialUrl = credentialUrl;
        this.status = status;
        this.order = order;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }
}
