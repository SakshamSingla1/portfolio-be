package com.portfolio.entities;

import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "profile_updated", def = "{ 'id': 1, 'updatedAt': -1 }")
public class Profile {
    @Id
    private String id;
    private String fullName;

    @Indexed(unique = true)
    private String userName;
    private String title;
    private String aboutMe;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String phone;
    private String location;
    private String password;
    private String profileImageUrl;
    private String profileImagePublicId;
    private String aboutMeImageUrl;
    private String aboutMeImagePublicId;
    private String logoUrl;
    private String logoPublicId;
    private String role;
    private StatusEnum status;
    private String themeName;
    private VerificationStatusEnum emailVerified;
    private VerificationStatusEnum phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
