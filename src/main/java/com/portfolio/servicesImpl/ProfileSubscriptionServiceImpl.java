package com.portfolio.servicesImpl;

import com.portfolio.dao.subscription.ProfileSubscriptionDao;
import com.portfolio.dao.subscription.SubscriptionPlanDao;
import com.portfolio.dao.subscription.SubscriptionPlanNavLinkDao;
import com.portfolio.dtos.ProfileSubscription.ProfileSubscriptionResponseDTO;
import com.portfolio.entities.ProfileSubscription;
import com.portfolio.entities.SubscriptionPlan;
import com.portfolio.enums.BillingCycleEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.SubscriptionStatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ProfileSubscriptionService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileSubscriptionServiceImpl implements ProfileSubscriptionService {

    private final ProfileSubscriptionDao profileSubscriptionDao;
    private final SubscriptionPlanDao subscriptionPlanDao;
    private final SubscriptionPlanNavLinkDao subscriptionPlanNavLinkDao;
    private final Helper helper;

    @Override
    public ProfileSubscriptionResponseDTO getActiveSubscription(Long profileId) throws GenericException {
        ProfileSubscription subscription = profileSubscriptionDao.findByProfileId(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_SUBSCRIPTION_NOT_FOUND, "No subscription found for this profile"));
        return mapToResponseDTO(subscription);
    }

    @Override
    @Transactional
    public ProfileSubscriptionResponseDTO assignPlan(Long profileId, Long planId, BillingCycleEnum billingCycle, boolean autoRenew) throws GenericException {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SUBSCRIPTION_PLAN_NOT_FOUND, "Subscription plan not found"));

        ProfileSubscription subscription = profileSubscriptionDao.findByProfileId(profileId)
                .orElse(ProfileSubscription.builder().profileId(profileId).build());

        subscription.setPlanId(plan.getId());
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
        subscription.setBillingCycle(billingCycle != null ? billingCycle : BillingCycleEnum.MONTHLY);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(null);
        subscription.setAutoRenew(autoRenew);
        subscription.setCancelledAt(null);

        ProfileSubscription saved = profileSubscriptionDao.save(subscription);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional
    public void assignDefaultPlan(Long profileId) throws GenericException {
        Long defaultPlanId = subscriptionPlanDao.findByIsDefaultTrue()
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SUBSCRIPTION_PLAN_NOT_FOUND, "No default subscription plan is configured"))
                .getId();
        assignPlan(profileId, defaultPlanId, BillingCycleEnum.MONTHLY, false);
    }

    @Override
    public Set<Long> getEffectiveNavLinkIds(Long profileId) {
        Set<Long> effective = new HashSet<>(subscriptionPlanNavLinkDao.findUngatedNavLinkIds());
        try {
            profileSubscriptionDao.findByProfileId(profileId).ifPresent(subscription -> {
                if (subscription.getStatus() == SubscriptionStatusEnum.ACTIVE || subscription.getStatus() == SubscriptionStatusEnum.TRIALING) {
                    effective.addAll(
                            subscriptionPlanNavLinkDao.findByPlanId(subscription.getPlanId()).stream()
                                    .map(com.portfolio.entities.SubscriptionPlanNavLink::getNavLinkId)
                                    .collect(Collectors.toSet())
                    );
                }
            });
        } catch (Exception e) {
            log.error("Failed to resolve plan-gated nav links for profile {}: {}", profileId, e.getMessage(), e);
        }
        return effective;
    }

    private ProfileSubscriptionResponseDTO mapToResponseDTO(ProfileSubscription subscription) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(subscription.getPlanId()).orElse(null);
        ProfileSubscriptionResponseDTO responseDTO = ProfileSubscriptionResponseDTO.builder()
                .id(subscription.getId())
                .profileId(subscription.getProfileId())
                .planId(subscription.getPlanId())
                .planName(plan != null ? plan.getName() : null)
                .planCode(plan != null ? plan.getCode() : null)
                .status(subscription.getStatus())
                .billingCycle(subscription.getBillingCycle())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.isAutoRenew())
                .cancelledAt(subscription.getCancelledAt())
                .build();

        helper.setAudit(subscription, responseDTO);
        return responseDTO;
    }
}
