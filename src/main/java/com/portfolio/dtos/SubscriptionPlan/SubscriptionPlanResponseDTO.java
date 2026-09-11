package com.portfolio.dtos.SubscriptionPlan;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.dtos.Role.RoleMappedModule;
import com.portfolio.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SubscriptionPlanResponseDTO extends AuditableResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private BigDecimal priceMonthly;
    private BigDecimal priceYearly;
    private String currency;
    private boolean isDefault;
    private Integer sortOrder;
    private StatusEnum status;
    private List<RoleMappedModule> includedNavLinks;
}
