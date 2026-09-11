package com.portfolio.dao.subscription;

import com.portfolio.entities.SubscriptionPlanNavLink;
import com.portfolio.repositories.SubscriptionPlanNavLinkRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SubscriptionPlanNavLinkDao {

    private final SubscriptionPlanNavLinkRepository subscriptionPlanNavLinkRepository;

    public SubscriptionPlanNavLinkDao(SubscriptionPlanNavLinkRepository subscriptionPlanNavLinkRepository) {
        this.subscriptionPlanNavLinkRepository = subscriptionPlanNavLinkRepository;
    }

    public List<SubscriptionPlanNavLink> saveAll(List<SubscriptionPlanNavLink> navLinks) {
        return subscriptionPlanNavLinkRepository.saveAll(navLinks);
    }

    public List<SubscriptionPlanNavLink> findByPlanId(Long planId) {
        return subscriptionPlanNavLinkRepository.findByPlanId(planId);
    }

    @Transactional
    public void deleteByPlanId(Long planId) {
        subscriptionPlanNavLinkRepository.deleteByPlanId(planId);
    }

    public List<Long> findUngatedNavLinkIds() {
        return subscriptionPlanNavLinkRepository.findUngatedNavLinkIds();
    }
}
