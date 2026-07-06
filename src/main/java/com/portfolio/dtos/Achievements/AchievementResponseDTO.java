package com.portfolio.dtos.Achievements;

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
public class AchievementResponseDTO extends AuditableResponse {
    private Long id;
    private String title;
    private String description;
    private String issuer;
    private LocalDate achievedAt;
    private String proofUrl;
    private String proofPublicId;
    private Integer order;
    private StatusEnum status;

    public AchievementResponseDTO(Long id, String title,
                                  String description, String issuer, LocalDate achievedAt,
                                  String proofUrl, String proofPublicId, Integer order,
                                  StatusEnum status,LocalDateTime createdAt, LocalDateTime updatedAt,
                                  Long createdBy, Long updatedBy, String createdByName,
                                  String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.title = title;
        this.description = description;
        this.issuer = issuer;
        this.achievedAt = achievedAt;
        this.proofUrl = proofUrl;
        this.proofPublicId = proofPublicId;
        this.order = order;
        this.status = status;
    }
}
