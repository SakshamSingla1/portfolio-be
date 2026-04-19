package com.portfolio.dtos.ContactUs;

import lombok.Data;

@Data
public class ContactUsRequest {
    private String name;
    private String email;
    private String message;
    private String phone;
    private String profileId;
}
