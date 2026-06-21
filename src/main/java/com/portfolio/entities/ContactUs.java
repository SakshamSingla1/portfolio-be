package com.portfolio.entities;

import com.portfolio.enums.ContactUsStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_us")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String phone;

    @Column(name = "profile_id")
    private Long profileId;

    @Enumerated(EnumType.STRING)
    private ContactUsStatusEnum status;

    private LocalDateTime createdAt;
}
