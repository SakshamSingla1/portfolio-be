package com.portfolio.dtos.ProfileTemplate;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProfileTemplateResponse extends AuditableResponse {

    private Long profileId;

    private String username;

    private String templateKey;
}
