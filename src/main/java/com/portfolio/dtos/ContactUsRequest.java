package com.portfolio.dtos;

import lombok.Data;

@Data
public class ContactUsRequest {
    String name;
    String email;
    String message;
    String phone;
}
