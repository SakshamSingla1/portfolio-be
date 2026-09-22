package com.portfolio.services;

import com.portfolio.dtos.SubscriptionPlan.SubscriptionPlanPublicResponse;
import com.portfolio.dtos.SubscriptionPlan.SubscriptionPlanRequestDTO;
import com.portfolio.dtos.SubscriptionPlan.SubscriptionPlanResponseDTO;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface SubscriptionPlanService {
    SubscriptionPlanResponseDTO upsertPlan(Long id, SubscriptionPlanRequestDTO requestDTO) throws GenericException;
    SubscriptionPlanResponseDTO getPlanById(Long id) throws GenericException;
    Page<SubscriptionPlanResponseDTO> getAllPlans(String search, String status, Pageable pageable) throws GenericException;
    void deletePlan(Long id) throws GenericException;
    SubscriptionPlanResponseDTO upsertPlanNavLinks(Long planId, List<Long> navLinkIds) throws GenericException;
    Set<Long> getIncludedNavLinkIds(Long planId);
    Long getDefaultPlanId() throws GenericException;
    List<SubscriptionPlanPublicResponse> getActivePlansPublic();
}
