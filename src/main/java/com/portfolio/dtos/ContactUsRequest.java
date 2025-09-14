package com.portfolio.dtos;

import lombok.Data;

@Data
public class ContactUsRequest {
    private String name;
    private String email;
    private String message;
    private String phone;
    private Integer profileId;
}
