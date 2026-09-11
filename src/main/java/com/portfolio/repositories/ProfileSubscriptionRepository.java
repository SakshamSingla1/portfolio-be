package com.portfolio.repositories;

import com.portfolio.entities.ProfileSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileSubscriptionRepository extends JpaRepository<ProfileSubscription, Long> {

    Optional<ProfileSubscription> findByProfileId(Long profileId);

    boolean existsByPlanId(Long planId);
}
