package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.role.RoleDao;
import com.portfolio.dao.social_links.SocialLinksDao;
import com.portfolio.dtos.SocialLinks.SocialLinkRequestDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Role;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.NTService;
import com.portfolio.services.SocialLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLinkServiceImpl implements SocialLinkService {
    private final SocialLinksDao socialLinksDao;
    private final ProfileDao profileDao;
    private final RoleDao roleDao;
    private final NTService ntService;

    // Matches the bare apex domain (used by EmbedController's fallback) and any
    // *.portfoliosbuilder.com subdomain (the normal per-user portfolio URL).
    private static final Pattern PORTFOLIOSBUILDER_DOMAIN_PATTERN =
            Pattern.compile("^https?://([a-z0-9-]+\\.)?portfoliosbuilder\\.com(/.*)?$", Pattern.CASE_INSENSITIVE);

    private boolean isCustomDomain(String url) {
        return url != null && !PORTFOLIOSBUILDER_DOMAIN_PATTERN.matcher(url.trim()).matches();
    }

    private void notifyDomainUpdated(Long profileId, String newUrl) {
        try {
            Profile profile = profileDao.findById(profileId).orElse(null);
            if (profile != null) {
                ntService.sendNotification(
                        "PORTFOLIO-DOMAIN-UPDATED",
                        Map.of("fullName", profile.getFullName(), "domainUrl", newUrl),
                        profile.getEmail()
                );
            }
        } catch (Exception e) {
            log.warn("Failed to send portfolio-domain-updated email for profile {}: {}", profileId, e.getMessage());
        }
    }

    // A user pointing their Portfolio link at a domain other than
    // *.portfoliosbuilder.com means they intend to self-host — notify every
    // SUPER_ADMIN so one of them can reach out with the self-hosting repo.
    // One send per admin, each independently caught so one failure (or one
    // missing mailbox) never blocks the others — same pattern used by
    // WeeklyDigestScheduler for its multi-recipient broadcast.
    private void notifySuperAdminsOfCustomDomain(Long profileId, String customUrl) {
        try {
            Profile profile = profileDao.findById(profileId).orElse(null);
            if (profile == null) return;

            Role superAdminRole = roleDao.findByName("SUPER_ADMIN").orElse(null);
            if (superAdminRole == null) {
                log.warn("SUPER_ADMIN role not found — cannot notify admins of custom domain for profile {}", profileId);
                return;
            }

            List<Profile> superAdmins = profileDao.findAllByRoleIdAndStatus(superAdminRole.getId(), StatusEnum.ACTIVE);
            for (Profile admin : superAdmins) {
                try {
                    ntService.sendNotification(
                            "PORTFOLIO-CUSTOM-DOMAIN-DETECTED",
                            Map.of(
                                    "adminName", admin.getFullName(),
                                    "userFullName", profile.getFullName(),
                                    "userEmail", profile.getEmail(),
                                    "customDomainUrl", customUrl
                            ),
                            admin.getEmail()
                    );
                } catch (Exception e) {
                    log.warn("Failed to notify super admin {} of custom domain for profile {}: {}",
                            admin.getId(), profileId, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to process custom-domain super-admin notification for profile {}: {}", profileId, e.getMessage());
        }
    }

    @Override
    public SocialLinkResponseDTO createLink(SocialLinkRequestDTO requestDTO) throws GenericException {
        if (!profileDao.existsById(requestDTO.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found");
        }
        Optional<SocialLinks> deletedLink = socialLinksDao
                .findByProfileIdAndPlatformAndStatus(requestDTO.getProfileId(), requestDTO.getPlatform(), StatusEnum.DELETED);
        if (deletedLink.isPresent()) {
            SocialLinks link = deletedLink.get();
            link.setStatus(StatusEnum.ACTIVE);
            link.setUrl(requestDTO.getUrl());
            link.setOrder(requestDTO.getOrder() != null ? Integer.parseInt(requestDTO.getOrder()) : null);
            SocialLinks saved = socialLinksDao.save(link);
            if (saved.getPlatform() == PlatformEnum.PORTFOLIO && isCustomDomain(saved.getUrl())) {
                notifySuperAdminsOfCustomDomain(saved.getProfileId(), saved.getUrl());
            }
            return mapToResponse(saved);
        }
        if (socialLinksDao.existsByProfileIdAndPlatformAndStatusNot(
                requestDTO.getProfileId(),
                requestDTO.getPlatform(),
                StatusEnum.DELETED)) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_SOCIAL_LINK, "Social Link already exists");
        }
        SocialLinks socialLinks = SocialLinks.builder()
                .profileId(requestDTO.getProfileId())
                .platform(requestDTO.getPlatform())
                .url(requestDTO.getUrl())
                .order(requestDTO.getOrder() != null ? Integer.parseInt(requestDTO.getOrder()) : null)
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : StatusEnum.ACTIVE)
                .build();

        SocialLinks saved = socialLinksDao.save(socialLinks);
        if (saved.getPlatform() == PlatformEnum.PORTFOLIO && isCustomDomain(saved.getUrl())) {
            notifySuperAdminsOfCustomDomain(saved.getProfileId(), saved.getUrl());
        }
        return mapToResponse(saved);
    }

    @Override
    public SocialLinkResponseDTO updateLink(Long id, SocialLinkRequestDTO requestDTO) throws GenericException {
        SocialLinks socialLinks = socialLinksDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND,"Social link not found"));
        String previousUrl = socialLinks.getUrl();
        socialLinks.setUrl(requestDTO.getUrl());
        socialLinks.setOrder(requestDTO.getOrder() != null ? Integer.parseInt(requestDTO.getOrder()) : null);
        socialLinks.setStatus(requestDTO.getStatus());
        socialLinks.setUpdatedAt(LocalDateTime.now());
        SocialLinks updated = socialLinksDao.save(socialLinks);

        if (updated.getPlatform() == PlatformEnum.PORTFOLIO
                && updated.getUrl() != null
                && !updated.getUrl().equals(previousUrl)) {
            notifyDomainUpdated(updated.getProfileId(), updated.getUrl());
            if (isCustomDomain(updated.getUrl())) {
                notifySuperAdminsOfCustomDomain(updated.getProfileId(), updated.getUrl());
            }
        }

        return mapToResponse(updated);
    }

    @Override
    public List<SocialLinkResponseDTO> getByProfile(Long profileId) {
        return socialLinksDao
                .findByProfileIdAndStatusOrderByOrderAsc(profileId, StatusEnum.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Page<SocialLinkResponseDTO> getByProfile(Long profileId, StatusEnum status, Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        return socialLinksDao.findByCriteria(profileId, status, search, sortedPageable);
    }

    @Override
    public SocialLinkResponseDTO get(Long id) throws GenericException {
        return socialLinksDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND, "Social link not found"));
    }

    @Override
    public void delete(Long id) throws GenericException {
        SocialLinks socialLinks = socialLinksDao
                .findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND, "Social link not found"));
        socialLinks.setStatus(StatusEnum.DELETED);
        socialLinks.setUpdatedAt(LocalDateTime.now());
        socialLinksDao.save(socialLinks);
    }

    private SocialLinkResponseDTO mapToResponse(SocialLinks socialLinks) {
        return SocialLinkResponseDTO.builder()
                .id(socialLinks.getId())
                .platform(socialLinks.getPlatform())
                .url(socialLinks.getUrl())
                .order(socialLinks.getOrder())
                .status(socialLinks.getStatus())
                .createdAt(socialLinks.getCreatedAt())
                .updatedAt(socialLinks.getUpdatedAt())
                .build();
    }
}
