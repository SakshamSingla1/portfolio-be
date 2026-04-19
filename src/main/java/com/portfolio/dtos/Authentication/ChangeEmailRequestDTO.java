package com.portfolio.dtos.Authentication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeEmailRequestDTO {
    private String newEmail;
}
