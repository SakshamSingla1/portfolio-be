package com.portfolio.dtos.Profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyProfileAccessRequest {

    @NotBlank
    private String accessCode;
}
