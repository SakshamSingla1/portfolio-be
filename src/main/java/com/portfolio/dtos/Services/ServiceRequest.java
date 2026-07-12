package com.portfolio.dtos.Services;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceRequest {

    @NotBlank(message = "Title is required")
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
