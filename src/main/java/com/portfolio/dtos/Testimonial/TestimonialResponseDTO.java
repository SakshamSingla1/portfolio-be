package com.portfolio.dtos.Testimonial;

import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TestimonialResponseDTO {
    private String id;
    private String name;
    private String role;
    private String company;
    private String message;
    private String imageUrl;
    private String imageId;
    private String linkedInUrl;
    private String order;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
