package com.portfolio.services;

import com.portfolio.dtos.ProfileSubscription.ProfileSubscriptionResponseDTO;
import com.portfolio.enums.BillingCycleEnum;
import com.portfolio.exceptions.GenericException;

import java.util.Set;

public interface ProfileSubscriptionService {
    ProfileSubscriptionResponseDTO getActiveSubscription(Long profileId) throws GenericException;
    ProfileSubscriptionResponseDTO assignPlan(Long profileId, Long planId, BillingCycleEnum billingCycle, boolean autoRenew) throws GenericException;
    void assignDefaultPlan(Long profileId) throws GenericException;
    Set<Long> getEffectiveNavLinkIds(Long profileId);
}
