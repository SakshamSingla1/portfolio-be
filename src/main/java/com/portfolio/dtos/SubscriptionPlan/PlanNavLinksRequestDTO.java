package com.portfolio.dtos.SubscriptionPlan;

import lombok.Data;

import java.util.List;

@Data
public class PlanNavLinksRequestDTO {
    private List<Long> navLinkIds;
}
