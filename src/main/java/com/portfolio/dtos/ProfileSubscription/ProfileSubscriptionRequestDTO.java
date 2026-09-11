package com.portfolio.dtos.ProfileSubscription;

import com.portfolio.enums.BillingCycleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileSubscriptionRequestDTO {
    @NotNull(message = "Plan id is required")
    private Long planId;

    private BillingCycleEnum billingCycle;
    private boolean autoRenew;
}
