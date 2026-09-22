package com.portfolio.dao.subscription;

import com.portfolio.entities.SubscriptionPlan;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.SubscriptionPlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionPlanDao {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanDao(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    public SubscriptionPlan save(SubscriptionPlan plan) {
        return subscriptionPlanRepository.save(plan);
    }

    public Optional<SubscriptionPlan> findById(Long id) {
        return subscriptionPlanRepository.findById(id);
    }

    public Optional<SubscriptionPlan> findByName(String name) {
        return subscriptionPlanRepository.findByName(name);
    }

    public Optional<SubscriptionPlan> findByCode(String code) {
        return subscriptionPlanRepository.findByCode(code);
    }

    public Optional<SubscriptionPlan> findByIsDefaultTrue() {
        return subscriptionPlanRepository.findByIsDefaultTrue();
    }

    public boolean existsByName(String name) {
        return subscriptionPlanRepository.existsByName(name);
    }

    public boolean existsByCode(String code) {
        return subscriptionPlanRepository.existsByCode(code);
    }

    public void deleteById(Long id) {
        subscriptionPlanRepository.deleteById(id);
    }

    public Page<SubscriptionPlan> findByCriteria(String search, StatusEnum status, Pageable pageable) {
        return subscriptionPlanRepository.findByCriteria(search, status, pageable);
    }

    public List<SubscriptionPlan> findActiveOrderedBySortOrder() {
        return subscriptionPlanRepository.findByStatusOrderBySortOrderAsc(StatusEnum.ACTIVE);
    }

    public void clearDefaultExcept(Long id) {
        subscriptionPlanRepository.clearDefaultExcept(id);
    }
}
