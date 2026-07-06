package com.portfolio.dtos.Achievements;

import com.portfolio.enums.StatusEnum;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AchievementRequestDTO {
    private Long profileId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Issuer is required")
    private String issuer;

    @NotNull(message = "Achieved at is required")
    private LocalDate achievedAt;

    @NotBlank(message = "Proof URL is required")
    private String proofUrl;

    @NotBlank(message = "Proof public ID is required")
    private String proofPublicId;

    @NotBlank(message = "Order is required")
    private String order;
    private StatusEnum status;
}
