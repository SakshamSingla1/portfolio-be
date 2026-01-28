package com.portfolio.dtos.Achievements;

import com.portfolio.enums.StatusEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AchievementRequestDTO {
    private String profileId;
    private String title;
    private String description;
    private String issuer;
    private LocalDate achievedAt;
    private String proofUrl;
    private String proofPublicId;
    private String order;
    private StatusEnum status;
}
