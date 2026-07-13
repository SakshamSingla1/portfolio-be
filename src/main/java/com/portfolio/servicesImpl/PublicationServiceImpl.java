package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.publication.PublicationDao;
import com.portfolio.dtos.Publication.PublicationRequestDTO;
import com.portfolio.dtos.Publication.PublicationResponseDTO;
import com.portfolio.entities.Publication;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.PublicationService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicationServiceImpl implements PublicationService {

    private final PublicationDao publicationDao;
    private final ProfileDao profileDao;
    private final Helper helper;

    @Override
    public PublicationResponseDTO create(PublicationRequestDTO dto) throws GenericException {
        profileDao.findById(dto.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        Publication publication = Publication.builder()
                .profileId(dto.getProfileId())
                .title(dto.getTitle())
                .type(dto.getType())
                .url(dto.getUrl())
                .publisher(dto.getPublisher())
                .publishedDate(dto.getPublishedDate())
                .description(dto.getDescription())
                .coAuthors(dto.getCoAuthors())
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .build();
        Publication saved = publicationDao.save(publication);
        return mapToResponse(saved);
    }

    @Override
    public PublicationResponseDTO update(Long id, PublicationRequestDTO dto) throws GenericException {
        Publication existing = publicationDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PUBLICATION_NOT_FOUND, "Publication not found"));
        existing.setTitle(dto.getTitle());
        existing.setType(dto.getType());
        existing.setUrl(dto.getUrl());
        existing.setPublisher(dto.getPublisher());
        existing.setPublishedDate(dto.getPublishedDate());
        existing.setDescription(dto.getDescription());
        existing.setCoAuthors(dto.getCoAuthors());
        existing.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : existing.getSortOrder());
        Publication saved = publicationDao.save(existing);
        return mapToResponse(saved);
    }

    @Override
    public PublicationResponseDTO getById(Long id) throws GenericException {
        return publicationDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PUBLICATION_NOT_FOUND, "Publication not found"));
    }

    @Override
    public Page<PublicationResponseDTO> getAll(Long profileId, String search, Pageable pageable) {
        return publicationDao.findByCriteria(profileId, search, pageable);
    }

    @Override
    public Void deleteById(Long id) throws GenericException {
        if (!publicationDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.PUBLICATION_NOT_FOUND, "Publication not found");
        }
        publicationDao.deleteById(id);
        return null;
    }

    @Override
    public List<PublicationResponseDTO> getByProfile(Long profileId) {
        return publicationDao.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PublicationResponseDTO mapToResponse(Publication p) {
        PublicationResponseDTO dto = PublicationResponseDTO.builder()
                .id(p.getId())
                .profileId(p.getProfileId())
                .title(p.getTitle())
                .type(p.getType())
                .url(p.getUrl())
                .publisher(p.getPublisher())
                .publishedDate(p.getPublishedDate())
                .description(p.getDescription())
                .coAuthors(p.getCoAuthors())
                .sortOrder(p.getSortOrder())
                .build();
        helper.setAudit(p, dto);
        return dto;
    }
}
