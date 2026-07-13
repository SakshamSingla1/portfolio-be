package com.portfolio.dao.publication;

import com.portfolio.dtos.Publication.PublicationResponseDTO;
import com.portfolio.entities.Publication;
import com.portfolio.repositories.PublicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class PublicationDao {

    private final PublicationRepository publicationRepository;

    public PublicationDao(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public Publication save(Publication publication) {
        return publicationRepository.save(publication);
    }

    public Optional<Publication> findById(Long id) {
        return publicationRepository.findById(id);
    }

    public void deleteById(Long id) {
        publicationRepository.deleteById(id);
    }

    public Optional<PublicationResponseDTO> findDTOById(Long id) {
        return publicationRepository.findDTOById(id);
    }

    public Page<PublicationResponseDTO> findByCriteria(Long profileId, String search, Pageable pageable) {
        return publicationRepository.findByCriteria(profileId, search, pageable);
    }

    public List<Publication> findByProfileId(Long profileId) {
        return publicationRepository.findByProfileIdOrderBySortOrderAscPublishedDateDesc(profileId);
    }

    public long countByProfileId(Long profileId) {
        return publicationRepository.countByProfileId(profileId);
    }

    public boolean existsById(Long id) {
        return publicationRepository.existsById(id);
    }
}
