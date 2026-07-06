package com.portfolio.dtos.Landing;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LandingFaqRequest {
    @NotBlank(message = "Question is required")
    private String question;
    @NotBlank(message = "Answer is required")
    private String answer;
    private int sortOrder;
    private boolean isActive;
}
