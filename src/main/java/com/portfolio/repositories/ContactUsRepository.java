package com.portfolio.repositories;

import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.entities.ContactUs;
import com.portfolio.enums.ContactUsStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {

    @Query("""
            SELECT NEW com.portfolio.dtos.ContactUs.ContactUsResponse(
                cu.id, cu.name, cu.email, cu.message, cu.phone,
                cu.status, cu.createdAt, cu.replyMessage, cu.repliedAt
            ) FROM ContactUs cu
            WHERE (:profileId IS NULL OR cu.profileId = :profileId)
            AND (:search IS NULL OR :search = ''
                OR LOWER(cu.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(cu.email) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                OR LOWER(cu.phone) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            AND (:status IS NULL OR cu.status = :status)
    """)
    Page<ContactUsResponse> findByCriteria(
            @Param("profileId") Long profileId,
            @Param("search") String search,
            @Param("status") ContactUsStatusEnum status,
            Pageable pageable
    );

    @Query("""
            SELECT NEW com.portfolio.dtos.ContactUs.ContactUsResponse(
                cu.id, cu.name, cu.email, cu.message, cu.phone,
                cu.status, cu.createdAt, cu.replyMessage, cu.repliedAt
            ) FROM ContactUs cu
            WHERE cu.id = :id
    """)
    Optional<ContactUsResponse> findDTOById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE ContactUs c SET c.status = :status WHERE c.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") ContactUsStatusEnum status);

    List<ContactUs> findTop5ByProfileIdOrderByCreatedAtDesc(Long profileId);

    @Query("""
            SELECT NEW com.portfolio.dtos.ContactUs.ContactUsResponse(
                cu.id, cu.name, cu.email, cu.message, cu.phone,
                cu.status, cu.createdAt, cu.replyMessage, cu.repliedAt
            ) FROM ContactUs cu
            WHERE cu.profileId = :profileId
            ORDER BY cu.createdAt DESC
            LIMIT 5
            """)
    List<ContactUsResponse> findTop5DTOByProfileIdOrderByCreatedAtDesc(@Param("profileId") Long profileId);

    long countByProfileId(Long profileId);

    long countByStatusAndProfileId(ContactUsStatusEnum status, Long profileId);

    Optional<ContactUs> findTop1ByProfileIdOrderByCreatedAtDesc(Long profileId);
}
