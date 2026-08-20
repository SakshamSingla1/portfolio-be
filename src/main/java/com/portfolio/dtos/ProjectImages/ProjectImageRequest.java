package com.portfolio.dtos.ProjectImages;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectImageRequest {
    @NotBlank(message = "Image url is required")
    private String url;
    private String publicId;
}
