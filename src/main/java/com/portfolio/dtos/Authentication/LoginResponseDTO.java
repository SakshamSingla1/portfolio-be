package com.portfolio.dtos.Authentication;

import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.dtos.NavLinks.GroupedNavLinkResponseDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponseDTO {
    private String id;
    private String fullName;
    private String userName;
    private String phone;
    private String email;
    private String role;
    private String token;
    private StatusEnum status;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private ColorThemeResponseDTO defaultTheme;
    private List<GroupedNavLinkResponseDTO> navLinks;
}
