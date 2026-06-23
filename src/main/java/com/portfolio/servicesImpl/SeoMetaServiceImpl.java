package com.portfolio.servicesImpl;

import com.portfolio.dtos.SeoMeta.SeoMetaRequestDTO;
import com.portfolio.dtos.SeoMeta.SeoMetaResponseDTO;
import com.portfolio.entities.SeoMeta;
import com.portfolio.enums.PageKeyEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.SeoMetaRepository;
import com.portfolio.services.SeoMetaService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoMetaServiceImpl implements SeoMetaService {

    private final SeoMetaRepository seoMetaRepository;
    private final Helper helper;

    @Override
    public List<SeoMetaResponseDTO> getAllByProfile(String authHeader) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(authHeader);
        return seoMetaRepository.findAllByProfileId(profileId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public SeoMetaResponseDTO getByPageKey(String authHeader, PageKeyEnum pageKey) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(authHeader);
        return seoMetaRepository.findByProfileIdAndPageKey(profileId, pageKey)
                .map(this::toDTO)
                .orElse(SeoMetaResponseDTO.builder().pageKey(pageKey).build());
    }

    @Override
    @Transactional
    public SeoMetaResponseDTO upsert(String authHeader, SeoMetaRequestDTO dto) throws GenericException {
        Long profileId = helper.getProfileIdFromHeader(authHeader);
        SeoMeta entity = seoMetaRepository
                .findByProfileIdAndPageKey(profileId, dto.getPageKey())
                .orElse(SeoMeta.builder()
                        .profileId(profileId)
                        .pageKey(dto.getPageKey())
                        .status(StatusEnum.ACTIVE)
                        .build());

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setKeywords(dto.getKeywords());
        entity.setOgTitle(dto.getOgTitle());
        entity.setOgDescription(dto.getOgDescription());
        entity.setOgImageUrl(dto.getOgImageUrl());
        entity.setCanonicalUrl(dto.getCanonicalUrl());
        entity.setIndexable(dto.getIndexable() != null ? dto.getIndexable() : true);
        entity.setFollowLinks(dto.getFollowLinks() != null ? dto.getFollowLinks() : true);

        return toDTO(seoMetaRepository.save(entity));
    }

    private SeoMetaResponseDTO toDTO(SeoMeta e) {
        return SeoMetaResponseDTO.builder()
                .id(e.getId())
                .pageKey(e.getPageKey())
                .title(e.getTitle())
                .description(e.getDescription())
                .keywords(e.getKeywords())
                .ogTitle(e.getOgTitle())
                .ogDescription(e.getOgDescription())
                .ogImageUrl(e.getOgImageUrl())
                .canonicalUrl(e.getCanonicalUrl())
                .indexable(e.getIndexable())
                .followLinks(e.getFollowLinks())
                .build();
    }
}
