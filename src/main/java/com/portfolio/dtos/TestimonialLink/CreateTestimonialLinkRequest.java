package com.portfolio.dtos.TestimonialLink;

import lombok.Data;

@Data
public class CreateTestimonialLinkRequest {

    private String requesterName;
    private String requesterEmail;
    private Integer expiryDays;
}
