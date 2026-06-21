package com.portfolio.dtos.File;

import com.portfolio.enums.ResourceTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FileAssetDTO {
    private String id;
    private String location;
    private String path;
    private String resourceId;
    private ResourceTypeEnum resourceType;
    private String mimeType;
    private String metaData;
    private LocalDateTime createdAt;
    private LocalDateTime validityFrom;
    private LocalDateTime validityTo;
    private String platform;
    private String createdBy;
    private String creatorName;
    private boolean isPrimary;
    private int sortOrder;
}
