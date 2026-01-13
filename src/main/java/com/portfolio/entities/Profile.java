package com.portfolio.entities;

import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    private String id;
    private String fullName;
    private String userName;
    private String title;
    private String aboutMe;
    private String email;
    private String phone;
    private String location;
    private String password;
    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;
    private String profileImageUrl;
    private String logoUrl;
    private String role;
    private StatusEnum status;
    private String themeName;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
