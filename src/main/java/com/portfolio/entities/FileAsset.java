package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.ResourceTypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_assets")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileAsset extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private String path;
    private String publicId;

    @Column(name = "resource_id")
    private Integer resourceId;

    @Enumerated(EnumType.STRING)
    private ResourceTypeEnum resourceType;

    private String mimeType;

    @Column(columnDefinition = "TEXT")
    private String metaData;

    private LocalDateTime validityFrom;
    private LocalDateTime validityTo;

    private String platform;

    private String creatorName;

    @Column(name = "is_primary")
    private boolean isPrimary;

    private int sortOrder;
}
