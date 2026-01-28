package com.portfolio.entities;

import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certifications {
    @Id
    private String id;
    private String profileId;
    private String title;
    private String issuer;
    private String credentialId;
    private String credentialUrl;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private StatusEnum status;
    private String order;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
