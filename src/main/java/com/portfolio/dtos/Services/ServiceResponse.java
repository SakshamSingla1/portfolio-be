package com.portfolio.dtos.Services;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ServiceResponse extends AuditableResponse {
    private Long id;
    private String title;
    private String description;
    private String icon;
    private String priceRange;
    private String deliveryTime;
    private Integer sortOrder;
    private Boolean isActive;
    private String bannerUrl;
    private String bannerPublicId;
}
