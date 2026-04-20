package com.portfolio.dtos.NavLinks;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NavLinkResponseDTO extends AuditableResponse {
    private String id;
    private String index;
    private String name;
    private String path;
    private String icon;
    private String navGroup;
    private StatusEnum status;
}
