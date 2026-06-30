package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.social_links.SocialLinksDao;
import com.portfolio.dtos.SocialLinks.SocialLinkRequestDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.SocialLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialLinkServiceImpl implements SocialLinkService {
    private final SocialLinksDao socialLinksDao;
    private final ProfileDao profileDao;

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
            link.setOrder(requestDTO.getOrder());
            SocialLinks saved = socialLinksDao.save(link);
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
                .order(requestDTO.getOrder())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : StatusEnum.ACTIVE)
                .build();

        SocialLinks saved = socialLinksDao.save(socialLinks);
        return mapToResponse(saved);
    }

    @Override
    public SocialLinkResponseDTO updateLink(Long id, SocialLinkRequestDTO requestDTO) throws GenericException {
        SocialLinks socialLinks = socialLinksDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND,"Social link not found"));
        socialLinks.setUrl(requestDTO.getUrl());
        socialLinks.setOrder(requestDTO.getOrder());
        socialLinks.setStatus(requestDTO.getStatus());
        socialLinks.setUpdatedAt(LocalDateTime.now());
        SocialLinks updated = socialLinksDao.save(socialLinks);
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
