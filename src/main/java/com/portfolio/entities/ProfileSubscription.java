package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.BillingCycleEnum;
import com.portfolio.enums.SubscriptionStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "profile_subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSubscription extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", unique = true, nullable = false)
    private Long profileId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycleEnum billingCycle;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "auto_renew")
    private boolean autoRenew;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "external_payment_ref")
    private String externalPaymentRef;
}
