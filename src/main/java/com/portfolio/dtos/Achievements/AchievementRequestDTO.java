package com.portfolio.dtos.Achievements;

import com.portfolio.enums.StatusEnum;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AchievementRequestDTO {
    @NotBlank(message = "Profile ID is required")
    private String profileId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Issuer is required")
    private String issuer;
    
    @NotNull(message = "Achieved date is required")
    private LocalDate achievedAt;
    
    private String proofUrl;
    
    private String proofPublicId;
    
    @NotBlank(message = "Order is required")
    private String order;
    
    @NotNull(message = "Status is required")
    private StatusEnum status;
}
