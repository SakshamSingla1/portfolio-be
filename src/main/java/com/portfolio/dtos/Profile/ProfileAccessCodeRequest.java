package com.portfolio.dtos.Profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileAccessCodeRequest {

    @NotBlank
    @Size(min = 4, max = 64)
    private String accessCode;
}
