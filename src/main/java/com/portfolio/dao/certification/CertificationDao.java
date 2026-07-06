package com.portfolio.dao.certification;

import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.entities.Certifications;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.CertificationsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class CertificationDao {

    private final CertificationsRepository certificationsRepository;

    public CertificationDao(CertificationsRepository certificationsRepository) {
        this.certificationsRepository = certificationsRepository;
    }

    public Certifications save(Certifications certification) {
        return certificationsRepository.save(certification);
    }

    public Optional<Certifications> findById(Long id) {
        return certificationsRepository.findById(id);
    }

    public void deleteById(Long id) {
        certificationsRepository.deleteById(id);
    }

    public boolean existsByProfileIdAndOrder(Long profileId, Integer order) {
        return certificationsRepository.existsByProfileIdAndOrder(profileId, order);
    }

    public boolean existsByProfileIdAndOrderAndIdNot(Long profileId, Integer order, Long id) {
        return certificationsRepository.existsByProfileIdAndOrderAndIdNot(profileId, order, id);
    }

    public List<Certifications> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum status) {
        return certificationsRepository.findByProfileIdAndStatusOrderByOrderAsc(profileId, status);
    }

    public long countByProfileId(Long profileId) {
        return certificationsRepository.countByProfileId(profileId);
    }

    public Optional<Certifications> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId) {
        return certificationsRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);
    }

    public Optional<CertificationResponseDTO> findDTOById(Long id) {
        return certificationsRepository.findDTOById(id);
    }

    public Page<CertificationResponseDTO> findByCriteria(Long profileId, String search, Pageable pageable) {
        return certificationsRepository.findByCriteria(profileId,search,pageable);
    }

    public boolean existsById(Long id) {
        return certificationsRepository.existsById(id);
    }
}
