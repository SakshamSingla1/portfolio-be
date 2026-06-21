package com.portfolio.dtos.Achievements;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AchievementResponseDTO extends AuditableResponse {
    private Long id;
    private Long profileId;
    private String title;
    private String description;
    private String issuer;
    private LocalDate achievedAt;
    private String proofUrl;
    private String proofPublicId;
    private String order;
    private StatusEnum status;
}
