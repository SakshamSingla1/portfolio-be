package com.portfolio.dtos.SubscriptionPlan;

import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionPlanRequestDTO {
    @NotBlank(message = "Plan name is required")
    private String name;

    @NotBlank(message = "Plan code is required")
    private String code;

    private String description;
    private BigDecimal priceMonthly;
    private BigDecimal priceYearly;
    private String currency;
    private boolean isDefault;
    private Integer sortOrder;
    private StatusEnum status;
}
