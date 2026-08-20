package com.portfolio.dtos.TestimonialLink;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class CreateTestimonialLinkRequest {

    // Both genuinely optional by design (createLink falls back to "there" for
    // the name and skips the invite email entirely when no email is given) —
    // only validate format when a value is actually supplied.
    private String requesterName;
    @Email(message = "Requester email must be a valid email address")
    private String requesterEmail;
    private Integer expiryDays;
}
