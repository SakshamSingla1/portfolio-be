package com.portfolio.dtos;

//import com.careHive.entities.ColorTheme;
//import com.careHive.entities.NavLink;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import lombok.Builder;
import lombok.Data;

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
//    private List<ColorTheme> themes;
//    private ColorTheme defaultTheme;
//    private List<NavLink> navLinks;
}
