package com.portfolio.repositories;

import com.portfolio.entities.ProfileThemeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileThemeMappingRepository extends JpaRepository<ProfileThemeMapping, Long> {

    Optional<ProfileThemeMapping> findByProfileId(Long profileId);

    boolean existsByProfileId(Long profileId);

    void deleteByProfileId(Long profileId);

    List<ProfileThemeMapping> findAllByThemeId(Long themeId);

    long countByThemeId(Long themeId);
}
