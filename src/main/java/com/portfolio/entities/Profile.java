package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.StatusEnum;
import com.portfolio.enums.VerificationStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String userName;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String aboutMe;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String location;
    private String password;
    private String profileImageUrl;
    private String profileImagePublicId;
    private String aboutMeImageUrl;
    private String aboutMeImagePublicId;
    private String logoUrl;
    private String logoPublicId;
    private Long roleId;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Enumerated(EnumType.STRING)
    private VerificationStatusEnum emailVerified;

    @Enumerated(EnumType.STRING)
    private VerificationStatusEnum phoneVerified;

    private boolean isTwoFactorEnabled;
    private String totpSecret;
}
