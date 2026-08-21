package com.portfolio.dtos.Help;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HelpFaqRequest {
    @NotBlank(message = "Question is required")
    private String question;
    @NotBlank(message = "Answer is required")
    private String answer;
    private int sortOrder;
    private boolean isActive;
}
