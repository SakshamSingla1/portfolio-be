package com.portfolio.dtos.Certifications;

import com.portfolio.enums.StatusEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificationRequestDTO {
    private String profileId;
    private String title;
    private String issuer;
    private String credentialId;
    private String credentialUrl;
    private StatusEnum status;
    private String order;
    private LocalDate issueDate;
    private LocalDate expiryDate;
}
