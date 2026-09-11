package com.portfolio.dao.subscription;

import com.portfolio.entities.ProfileSubscription;
import com.portfolio.repositories.ProfileSubscriptionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProfileSubscriptionDao {

    private final ProfileSubscriptionRepository profileSubscriptionRepository;

    public ProfileSubscriptionDao(ProfileSubscriptionRepository profileSubscriptionRepository) {
        this.profileSubscriptionRepository = profileSubscriptionRepository;
    }

    public ProfileSubscription save(ProfileSubscription profileSubscription) {
        return profileSubscriptionRepository.save(profileSubscription);
    }

    public Optional<ProfileSubscription> findByProfileId(Long profileId) {
        return profileSubscriptionRepository.findByProfileId(profileId);
    }

    public boolean existsByPlanId(Long planId) {
        return profileSubscriptionRepository.existsByPlanId(planId);
    }
}
