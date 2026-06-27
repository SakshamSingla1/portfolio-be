package com.portfolio.repositories;

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

    Page<Resume> findByStatus(StatusEnum status, Pageable pageable);

    Page<Resume> findByProfileId(Long profileId, Pageable pageable);

    Page<Resume> findByStatusAndProfileId(StatusEnum status, Long profileId, Pageable pageable);

    @Query("SELECT r FROM Resume r, FileAsset f WHERE f.resourceId = CAST(r.id AS string) AND f.resourceType = com.portfolio.enums.ResourceTypeEnum.RESUME AND LOWER(f.metaData) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Resume> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT r FROM Resume r, FileAsset f WHERE f.resourceId = CAST(r.id AS string) AND f.resourceType = com.portfolio.enums.ResourceTypeEnum.RESUME AND LOWER(f.metaData) LIKE LOWER(CONCAT('%', :search, '%')) AND r.status = :status")
    Page<Resume> searchByStatus(@Param("search") String search, @Param("status") StatusEnum status, Pageable pageable);

    @Query("SELECT r FROM Resume r, FileAsset f WHERE f.resourceId = CAST(r.id AS string) AND f.resourceType = com.portfolio.enums.ResourceTypeEnum.RESUME AND LOWER(f.metaData) LIKE LOWER(CONCAT('%', :search, '%')) AND r.profileId = :profileId")
    Page<Resume> searchByProfileId(@Param("search") String search, @Param("profileId") Long profileId, Pageable pageable);

    @Query("SELECT r FROM Resume r, FileAsset f WHERE f.resourceId = CAST(r.id AS string) AND f.resourceType = com.portfolio.enums.ResourceTypeEnum.RESUME AND LOWER(f.metaData) LIKE LOWER(CONCAT('%', :search, '%')) AND r.status = :status AND r.profileId = :profileId")
    Page<Resume> searchByProfileIdAndStatus(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            @Param("profileId") Long profileId,
            Pageable pageable
    );
}
