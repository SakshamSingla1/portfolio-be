package com.portfolio.servicesImpl;

import com.portfolio.dao.nav_link.NavLinkDao;
import com.portfolio.dao.subscription.ProfileSubscriptionDao;
import com.portfolio.dao.subscription.SubscriptionPlanDao;
import com.portfolio.dao.subscription.SubscriptionPlanNavLinkDao;
import com.portfolio.dtos.Role.RoleMappedModule;
import com.portfolio.dtos.SubscriptionPlan.SubscriptionPlanRequestDTO;
import com.portfolio.dtos.SubscriptionPlan.SubscriptionPlanResponseDTO;
import com.portfolio.entities.NavLink;
import com.portfolio.entities.SubscriptionPlan;
import com.portfolio.entities.SubscriptionPlanNavLink;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.SubscriptionPlanService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanDao subscriptionPlanDao;
    private final SubscriptionPlanNavLinkDao subscriptionPlanNavLinkDao;
    private final ProfileSubscriptionDao profileSubscriptionDao;
    private final NavLinkDao navLinkDao;
    private final Helper helper;

    @Override
    @Transactional
    public SubscriptionPlanResponseDTO upsertPlan(Long id, SubscriptionPlanRequestDTO requestDTO) throws GenericException {
        SubscriptionPlan plan;
        if (id != null) {
            plan = subscriptionPlanDao.findById(id)
                    .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SUBSCRIPTION_PLAN_NOT_FOUND, "Subscription plan not found"));
            if (!plan.getName().equals(requestDTO.getName()) && subscriptionPlanDao.existsByName(requestDTO.getName())) {
                throw new GenericException(ExceptionCodeEnum.DUPLICATE_SUBSCRIPTION_PLAN, "Plan with this name already exists");
            }
            if (!plan.getCode().equals(requestDTO.getCode()) && subscriptionPlanDao.existsByCode(requestDTO.getCode())) {
                throw new GenericException(ExceptionCodeEnum.DUPLICATE_SUBSCRIPTION_PLAN, "Plan with this code already exists");
            }
        } else {
            if (subscriptionPlanDao.existsByName(requestDTO.getName())) {
                throw new GenericException(ExceptionCodeEnum.DUPLICATE_SUBSCRIPTION_PLAN, "Plan with this name already exists");
            }
            if (subscriptionPlanDao.existsByCode(requestDTO.getCode())) {
                throw new GenericException(ExceptionCodeEnum.DUPLICATE_SUBSCRIPTION_PLAN, "Plan with this code already exists");
            }
            plan = SubscriptionPlan.builder().build();
        }

        plan.setName(requestDTO.getName());
        plan.setCode(requestDTO.getCode());
        plan.setDescription(requestDTO.getDescription());
        plan.setPriceMonthly(requestDTO.getPriceMonthly());
        plan.setPriceYearly(requestDTO.getPriceYearly());
        plan.setCurrency(requestDTO.getCurrency());
        plan.setSortOrder(requestDTO.getSortOrder());
        plan.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : StatusEnum.ACTIVE);
        plan.setDefault(requestDTO.isDefault());

        SubscriptionPlan savedPlan = subscriptionPlanDao.save(plan);
        if (savedPlan.isDefault()) {
            subscriptionPlanDao.clearDefaultExcept(savedPlan.getId());
        }
        return mapToResponseDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanResponseDTO getPlanById(Long id) throws GenericException {
        SubscriptionPlan plan = subscriptionPlanDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SUBSCRIPTION_PLAN_NOT_FOUND, "Subscription plan not found"));
        return mapToResponseDTO(plan);
    }

    @Override
    public Page<SubscriptionPlanResponseDTO> getAllPlans(String search, String status, Pageable pageable) throws GenericException {
        StatusEnum statusEnum = (status != null && !status.trim().isEmpty()) ? StatusEnum.valueOf(status) : null;
        return subscriptionPlanDao.findByCriteria(search, statusEnum, pageable).map(this::mapToResponseDTO);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) throws GenericException {
        SubscriptionPlan plan = subscriptionPlanDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SUBSCRIPTION_PLAN_NOT_FOUND, "Subscription plan not found"));
        if (profileSubscriptionDao.existsByPlanId(id)) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST,
                    "Plan is currently assigned to one or more profiles and cannot be deleted");
        }
        subscriptionPlanNavLinkDao.deleteByPlanId(id);
        subscriptionPlanDao.deleteById(plan.getId());
    }

    @Override
    @Transactional
    public SubscriptionPlanResponseDTO upsertPlanNavLinks(Long planId, List<Long> navLinkIds) throws GenericException {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SUBSCRIPTION_PLAN_NOT_FOUND, "Subscription plan not found"));

        subscriptionPlanNavLinkDao.deleteByPlanId(planId);

        if (navLinkIds != null && !navLinkIds.isEmpty()) {
            List<SubscriptionPlanNavLink> mappings = navLinkIds.stream()
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .map(navLinkId -> SubscriptionPlanNavLink.builder()
                            .planId(planId)
                            .navLinkId(navLinkId)
                            .build())
                    .collect(Collectors.toList());
            subscriptionPlanNavLinkDao.saveAll(mappings);
        }

        return mapToResponseDTO(plan);
    }

    @Override
    public Set<Long> getIncludedNavLinkIds(Long planId) {
        return subscriptionPlanNavLinkDao.findByPlanId(planId).stream()
                .map(SubscriptionPlanNavLink::getNavLinkId)
                .collect(Collectors.toSet());
    }

    @Override
    public Long getDefaultPlanId() throws GenericException {
        return subscriptionPlanDao.findByIsDefaultTrue()
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SUBSCRIPTION_PLAN_NOT_FOUND, "No default subscription plan is configured"))
                .getId();
    }

    private SubscriptionPlanResponseDTO mapToResponseDTO(SubscriptionPlan plan) {
        List<SubscriptionPlanNavLink> mappings = subscriptionPlanNavLinkDao.findByPlanId(plan.getId());
        Map<Long, NavLink> navLinkMap = navLinkDao.findAllByIdAsMap(
                mappings.stream().map(SubscriptionPlanNavLink::getNavLinkId).collect(Collectors.toList())
        );
        List<RoleMappedModule> includedNavLinks = mappings.stream()
                .map(m -> {
                    NavLink navLink = navLinkMap.get(m.getNavLinkId());
                    return RoleMappedModule.builder()
                            .navLinkId(m.getNavLinkId())
                            .navLinkName(navLink != null ? navLink.getName() : null)
                            .build();
                })
                .collect(Collectors.toList());

        SubscriptionPlanResponseDTO responseDTO = SubscriptionPlanResponseDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .code(plan.getCode())
                .description(plan.getDescription())
                .priceMonthly(plan.getPriceMonthly())
                .priceYearly(plan.getPriceYearly())
                .currency(plan.getCurrency())
                .isDefault(plan.isDefault())
                .sortOrder(plan.getSortOrder())
                .status(plan.getStatus())
                .includedNavLinks(includedNavLinks)
                .build();

        helper.setAudit(plan, responseDTO);
        return responseDTO;
    }
}
