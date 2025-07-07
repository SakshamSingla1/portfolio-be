package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ContactUsResponse {
    private int id;
    private String name;
    private String email;
    private String message;
    private String phone;
    private Date created;
}
