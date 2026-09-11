package com.portfolio.dtos.ProfileSubscription;

import com.portfolio.dtos.AuditableResponse;
import com.portfolio.enums.BillingCycleEnum;
import com.portfolio.enums.SubscriptionStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ProfileSubscriptionResponseDTO extends AuditableResponse {
    private Long id;
    private Long profileId;
    private Long planId;
    private String planName;
    private String planCode;
    private SubscriptionStatusEnum status;
    private BillingCycleEnum billingCycle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean autoRenew;
    private LocalDateTime cancelledAt;
}
