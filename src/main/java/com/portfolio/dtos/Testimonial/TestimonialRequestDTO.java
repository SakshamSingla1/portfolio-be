package com.portfolio.dtos.Testimonial;

import com.portfolio.enums.StatusEnum;
import lombok.Data;

@Data
public class TestimonialRequestDTO {
    private String profileId;
    private String name;
    private String role;
    private String company;
    private String message;
    private String imageUrl;
    private String imageId;
    private String linkedInUrl;
    private String order;
    private StatusEnum status;
}
