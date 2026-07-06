package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "resumes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Resume extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id")
    private Long profileId;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "public_id", columnDefinition = "TEXT")
    private String publicId;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
