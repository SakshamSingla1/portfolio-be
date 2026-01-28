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

@Document(collection = "achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Achievements {
    @Id
    private String id;
    private String profileId;
    private String title;
    private String description;
    private String issuer;
    private LocalDate achievedAt;
    private String proofUrl;
    private String proofPublicId;
    private String order;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

