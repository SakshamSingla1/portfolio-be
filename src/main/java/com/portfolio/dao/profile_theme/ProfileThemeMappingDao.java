package com.portfolio.dao.profile_theme;

import com.portfolio.entities.ProfileThemeMapping;
import com.portfolio.repositories.ProfileThemeMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ProfileThemeMappingDao {

    private final ProfileThemeMappingRepository profileThemeMappingRepository;

    public ProfileThemeMappingDao(ProfileThemeMappingRepository profileThemeMappingRepository) {
        this.profileThemeMappingRepository = profileThemeMappingRepository;
    }

    public ProfileThemeMapping save(ProfileThemeMapping mapping) {
        return profileThemeMappingRepository.save(mapping);
    }

    public Optional<ProfileThemeMapping> findById(Long id) {
        return profileThemeMappingRepository.findById(id);
    }

    public void deleteById(Long id) {
        profileThemeMappingRepository.deleteById(id);
    }

    public Optional<ProfileThemeMapping> findByProfileId(Long profileId) {
        return profileThemeMappingRepository.findByProfileId(profileId);
    }

    public boolean existsByProfileId(Long profileId) {
        return profileThemeMappingRepository.existsByProfileId(profileId);
    }

    public void deleteByProfileId(Long profileId) {
        profileThemeMappingRepository.deleteByProfileId(profileId);
    }

    public List<ProfileThemeMapping> findAllByThemeId(Long themeId) {
        return profileThemeMappingRepository.findAllByThemeId(themeId);
    }

    public long countByThemeId(Long themeId) {
        return profileThemeMappingRepository.countByThemeId(themeId);
    }
}
