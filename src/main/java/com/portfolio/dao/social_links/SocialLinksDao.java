package com.portfolio.dao.social_links;

import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.SocialLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class SocialLinksDao {

    private final SocialLinkRepository socialLinkRepository;

    public SocialLinksDao(SocialLinkRepository socialLinkRepository) {
        this.socialLinkRepository = socialLinkRepository;
    }

    public SocialLinks save(SocialLinks socialLinks) {
        return socialLinkRepository.save(socialLinks);
    }

    public Optional<SocialLinks> findById(Long id) {
        return socialLinkRepository.findById(id);
    }

    public void deleteById(Long id) {
        socialLinkRepository.deleteById(id);
    }

    public Optional<SocialLinks> findByPlatformAndUrl(PlatformEnum platform, String url) {
        return socialLinkRepository.findByPlatformAndUrl(platform, url);
    }

    public Optional<SocialLinks> findByProfileIdAndPlatformAndStatus(Long profileId, PlatformEnum platform, StatusEnum status) {
        return socialLinkRepository.findByProfileIdAndPlatformAndStatus(profileId, platform, status);
    }

    public boolean existsByProfileIdAndPlatformAndStatusNot(Long profileId, PlatformEnum platform, StatusEnum status) {
        return socialLinkRepository.existsByProfileIdAndPlatformAndStatusNot(profileId, platform, status);
    }

    public Optional<SocialLinkResponseDTO> findDTOById(Long id) {
        return socialLinkRepository.findDTOById(id);
    }

    public Page<SocialLinkResponseDTO> findByCriteria(Long profileId, StatusEnum status, String search, Pageable pageable) {
        return socialLinkRepository.findByCriteria(profileId, status, search, pageable);
    }

    public long countByProfileId(Long profileId) {
        return socialLinkRepository.countByProfileId(profileId);
    }

    public List<SocialLinks> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum status) {
        return socialLinkRepository.findByProfileIdAndStatusOrderByOrderAsc(profileId, status);
    }

    public Optional<SocialLinks> findPortfolioUrlByPlatformAndStatus(PlatformEnum platform, StatusEnum status) {
        return socialLinkRepository.findPortfolioUrlByPlatformAndStatus(platform, status);
    }

    public Optional<SocialLinks> findByPlatformAndUrlContainingHost(PlatformEnum platform, String host) {
        return socialLinkRepository.findByPlatformAndUrlContainingHost(platform, host);
    }

}
