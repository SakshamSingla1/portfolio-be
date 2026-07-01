package com.portfolio.repositories;

import com.portfolio.dtos.Resume.ResumeUploadResponseDTO;
import com.portfolio.entities.Resume;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByProfileId(Long profileId);

    Optional<Resume> findByProfileIdAndStatus(Long profileId, StatusEnum status);

    // idx_resumes_profile_id, idx_resumes_status, idx_file_assets_resource
    @Query("""
            SELECT NEW com.portfolio.dtos.Resume.ResumeUploadResponseDTO(
                r.id, fa.metaData, fa.path, fa.publicId, r.status, r.updatedAt
            ) FROM Resume r
            LEFT JOIN FileAsset fa ON fa.resourceId = r.id AND fa.resourceType = 'RESUME'
            WHERE (:profileId IS NULL OR r.profileId = :profileId)
            AND (:status IS NULL OR r.status = :status)
            AND (:search IS NULL OR :search = ''
                OR LOWER(fa.metaData) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            """)
    Page<ResumeUploadResponseDTO> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("status") StatusEnum status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT NEW com.portfolio.dtos.Resume.ResumeUploadResponseDTO(
                r.id, fa.metaData, fa.path, fa.publicId, r.status, r.updatedAt
            ) FROM Resume r
            LEFT JOIN FileAsset fa ON fa.resourceId = r.id AND fa.resourceType = 'RESUME'
            WHERE r.id = :id
            """)
    Optional<ResumeUploadResponseDTO> findDTOById(@Param("id") Long id);
}
