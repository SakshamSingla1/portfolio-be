package com.portfolio.servicesImpl;

import com.portfolio.dtos.SocialLinks.SocialLinkRequestDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.SocialLinkRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialLinkServiceImpl implements SocialLinkService {
    private final SocialLinkRepository socialLinkRepository;
    private final ProfileRepository profileRepository;

    @Override
    public SocialLinkResponseDTO createLink(SocialLinkRequestDTO requestDTO) throws GenericException {
        if (!profileRepository.existsById(requestDTO.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found");
        }
        Optional<SocialLinks> deletedLink = socialLinkRepository
                .findByProfileIdAndPlatformAndStatus(requestDTO.getProfileId(), requestDTO.getPlatform(), StatusEnum.DELETED);
        if (deletedLink.isPresent()) {
            SocialLinks link = deletedLink.get();
            link.setStatus(StatusEnum.ACTIVE);
            link.setUrl(requestDTO.getUrl());
            link.setOrder(requestDTO.getOrder());
            link.setUpdatedAt(LocalDateTime.now());
            SocialLinks saved = socialLinkRepository.save(link);
            return mapToResponse(saved);
        }
        if (socialLinkRepository.existsByProfileIdAndPlatformAndStatusNot(
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SocialLinks saved = socialLinkRepository.save(socialLinks);
        return mapToResponse(saved);
    }

    @Override
    public SocialLinkResponseDTO updateLink(String id, SocialLinkRequestDTO requestDTO) throws GenericException {
        SocialLinks socialLinks = socialLinkRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND,"Social link not found"));
        socialLinks.setUrl(requestDTO.getUrl());
        socialLinks.setOrder(requestDTO.getOrder());
        socialLinks.setStatus(requestDTO.getStatus());
        socialLinks.setUpdatedAt(LocalDateTime.now());
        SocialLinks updated = socialLinkRepository.save(socialLinks);
        return mapToResponse(updated);
    }

    @Override
    public List<SocialLinkResponseDTO> getByProfile(String profileId) {
        return socialLinkRepository
                .findByProfileIdAndStatusOrderByOrderAsc(profileId, StatusEnum.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Page<SocialLinkResponseDTO> getByProfile(String profileId, StatusEnum status, Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatus = status != null;
        boolean hasProfileId = profileId != null;
        Page<SocialLinks> socialLinks;
        if(hasStatus && hasSearch && hasProfileId){
            socialLinks = socialLinkRepository.searchByProfileIdAndStatus(search,status,profileId,sortedPageable);
        }else if(hasStatus && hasSearch){
            socialLinks = socialLinkRepository.searchByStatus(search,status,sortedPageable);
        }else if(hasSearch && hasProfileId){
            socialLinks = socialLinkRepository.searchByProfileId(search,profileId,sortedPageable);
        }else if(hasStatus && hasProfileId){
            socialLinks = socialLinkRepository.findByStatusAndProfileId(status,profileId,sortedPageable);
        }else if(hasStatus){
            socialLinks = socialLinkRepository.findByStatus(status,sortedPageable);
        }else if(hasProfileId){
            socialLinks = socialLinkRepository.findByProfileId(profileId,sortedPageable);
        }else if(hasSearch){
            socialLinks = socialLinkRepository.search(search,sortedPageable);
        }else{
            socialLinks = socialLinkRepository.findAll(sortedPageable);
        }
        return socialLinks.map(this::mapToResponse);
    }

    @Override
    public SocialLinkResponseDTO get(String id) throws GenericException {
        SocialLinks socialLinks = socialLinkRepository
                .findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND, "Social link not found"));
        return mapToResponse(socialLinks);
    }

    @Override
    public void delete(String id) throws GenericException {
        SocialLinks socialLinks = socialLinkRepository
                .findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND, "Social link not found"));
        socialLinks.setStatus(StatusEnum.DELETED);
        socialLinks.setUpdatedAt(LocalDateTime.now());
        socialLinkRepository.save(socialLinks);
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
