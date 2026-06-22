package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.ContactUsStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact_us")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactUs extends Auditable {

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
}
