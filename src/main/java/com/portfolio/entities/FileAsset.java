package com.portfolio.entities;

import com.portfolio.enums.ResourceTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "file_assets")
@CompoundIndex(def = "{'resourceId': 1, 'resourceType': 1}")
public class FileAsset {

    @Id
    private String id;

    private String location;
    private String path;
    private String publicId;

    private String resourceId;
    private ResourceTypeEnum resourceType;

    private String mimeType;
    private String metaData;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime validityFrom;
    private LocalDateTime validityTo;

    private String platform;

    @CreatedBy
    private String createdBy;

    private String creatorName;

    private boolean isPrimary;
    private int sortOrder;
}
