package com.portfolio.dtos.TestimonialLink;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TestimonialSubmitRequest {

    @NotBlank
    private String name;

    private String role;
    private String company;

    @NotBlank
    private String message;

    private String linkedInUrl;
}
