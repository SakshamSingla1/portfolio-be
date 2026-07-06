package com.portfolio.dtos.ContactUs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ContactUsRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Message is required")
    private String message;

    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Invalid phone number")
    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Profile ID is required")
    private Long profileId;
}
