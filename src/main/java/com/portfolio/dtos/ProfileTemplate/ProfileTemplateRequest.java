package com.portfolio.dtos.ProfileTemplate;

import com.portfolio.enums.TemplateKeyEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileTemplateRequest {
    @NotNull(message = "Template key is required")
    private TemplateKeyEnum templateKey;
}
