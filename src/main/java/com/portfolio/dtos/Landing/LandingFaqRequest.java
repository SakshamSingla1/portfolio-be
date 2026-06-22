package com.portfolio.dtos.Landing;

import lombok.Data;

@Data
public class LandingFaqRequest {
    private String question;
    private String answer;
    private int sortOrder;
    private boolean isActive;
}
