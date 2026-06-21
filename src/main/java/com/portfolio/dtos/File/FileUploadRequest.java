package com.portfolio.dtos.File;

import com.portfolio.enums.ResourceTypeEnum;
import lombok.Data;

@Data
public class FileUploadRequest {
    private String resourceId;
    private ResourceTypeEnum resourceType;
    private boolean isPrimary;
    private int sortOrder;
    private String platform;
    private String metaData;
}
