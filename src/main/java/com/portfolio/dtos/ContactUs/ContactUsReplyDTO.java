package com.portfolio.dtos.ContactUs;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ContactUsReplyDTO {
    @NotBlank(message = "Message is required")
    private String message;
}
