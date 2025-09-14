package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="profile",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone")
        })
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fullName;
    private String title;
    private String aboutMe;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String location;
    private String password;

    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;
    private String profileImageUrl;
    private String logo;

    @Column(nullable = false)
    private String role = "ADMIN";

    private boolean verified;
}
