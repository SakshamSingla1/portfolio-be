package com.portfolio.dtos.Testimonial;

import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TestimonialRequestDTO {
    private Long profileId;
    @NotBlank(message = "Name is required")
    private String name;
    private String role;
    private String company;
    @NotBlank(message = "Message is required")
    private String message;
    private String imageUrl;
    private Long imageId;
    private String linkedInUrl;
    private String order;
    private StatusEnum status;
}
