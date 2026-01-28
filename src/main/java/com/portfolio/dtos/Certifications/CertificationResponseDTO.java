package com.portfolio.dtos.Certifications;

import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CertificationResponseDTO {
    private String id;
    private String title;
    private String issuer;
    private String credentialId;
    private String credentialUrl;
    private StatusEnum status;
    private String order;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
