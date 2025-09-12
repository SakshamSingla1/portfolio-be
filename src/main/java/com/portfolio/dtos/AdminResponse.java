package com.portfolio.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponse {
    private int id;
    private String fullName;
    private String email;
    private String role;
    private String token;
    private String message;
}
