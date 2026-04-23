package com.portfolio.repositories;

import com.portfolio.entities.ProfileThemeMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileThemeMappingRepository extends MongoRepository<ProfileThemeMapping, String> {

    Optional<ProfileThemeMapping> findByProfileId(String profileId);

    boolean existsByProfileId(String profileId);

    void deleteByProfileId(String profileId);

    List<ProfileThemeMapping> findAllByThemeId(String themeId);

    long countByThemeId(String themeId);
}