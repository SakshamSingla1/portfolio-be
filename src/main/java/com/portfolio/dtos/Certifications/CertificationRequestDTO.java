package com.portfolio.dtos.Certifications;

import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificationRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Issuer is required")
    private String issuer;
    private String credentialId;
    private String credentialUrl;
    private String credentialPublicId;
    private StatusEnum status;
    private String order;
    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Long profileId;
}
