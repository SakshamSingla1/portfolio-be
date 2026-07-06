package com.portfolio.dtos.Logos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LogoRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "URL is required")
    private String url;
}
