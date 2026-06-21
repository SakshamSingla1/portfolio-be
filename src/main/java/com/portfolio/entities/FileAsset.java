package com.portfolio.entities;

import com.portfolio.enums.ResourceTypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_assets")
@Data
@EntityListeners(AuditingEntityListener.class)
public class FileAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private String path;
    private String publicId;

    @Column(name = "resource_id")
    private String resourceId;

    @Enumerated(EnumType.STRING)
    private ResourceTypeEnum resourceType;

    private String mimeType;

    @Column(columnDefinition = "TEXT")
    private String metaData;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime validityFrom;
    private LocalDateTime validityTo;

    private String platform;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    private String creatorName;

    @Column(name = "is_primary")
    private boolean isPrimary;

    private int sortOrder;
}
