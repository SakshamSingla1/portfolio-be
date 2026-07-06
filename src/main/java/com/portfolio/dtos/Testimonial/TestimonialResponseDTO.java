package com.portfolio.dtos.Testimonial;

import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialResponseDTO {
    private Long id;
    private String name;
    private String role;
    private String company;
    private String message;
    private String imageUrl;
    private Long imageId;
    private String linkedInUrl;
    private Integer order;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
