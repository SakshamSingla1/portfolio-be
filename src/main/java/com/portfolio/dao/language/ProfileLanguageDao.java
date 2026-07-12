package com.portfolio.dao.language;

import com.portfolio.entities.ProfileLanguage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileLanguageDao extends JpaRepository<ProfileLanguage, Long> {
    Page<ProfileLanguage> findByProfileIdAndLanguageNameContainingIgnoreCase(Long profileId, String languageName, Pageable pageable);
    Page<ProfileLanguage> findByProfileId(Long profileId, Pageable pageable);
    List<ProfileLanguage> findByProfileIdOrderBySortOrderAsc(Long profileId);
}
