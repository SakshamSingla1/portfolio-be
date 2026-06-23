package com.portfolio.dtos.Authentication;

import lombok.Data;

@Data
public class TwoFactorVerifyDTO {
    private String pendingToken;
    private String totpCode;
}
