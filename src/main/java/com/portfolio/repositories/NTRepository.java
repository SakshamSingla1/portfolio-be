package com.portfolio.repositories;

import com.portfolio.dtos.NotificationTemplates.NotificationTemplateListResponseDTO;
import com.portfolio.entities.NotificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NTRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByTemplate(String template);

    @Query("""
            SELECT new com.portfolio.dtos.NotificationTemplates.NotificationTemplateListResponseDTO(
                nt.id, nt.subject, nt.template, nt.isSms, nt.isEmail, nt.isWhatsapp,
                nt.whatsappTemplateName, nt.templateGroupId, nt.createdAt, nt.updatedAt)
            FROM NotificationTemplate nt
            WHERE (:search = '' OR LOWER(nt.template) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            """)
    Page<NotificationTemplateListResponseDTO> findByCriteria(
            @Param("search") String search,
            Pageable pageable);

    @Query("""
            SELECT new com.portfolio.dtos.NotificationTemplates.NotificationTemplateListResponseDTO(
                nt.id, nt.subject, nt.template, nt.isSms, nt.isEmail, nt.isWhatsapp,
                nt.whatsappTemplateName, nt.templateGroupId, nt.createdAt, nt.updatedAt)
            FROM NotificationTemplate nt
            WHERE (:search = '' OR LOWER(nt.template) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
              AND nt.templateGroupId IN :templateGroupIds
            """)
    Page<NotificationTemplateListResponseDTO> findByCriteriaWithGroups(
            @Param("search") String search,
            @Param("templateGroupIds") List<Long> templateGroupIds,
            Pageable pageable);
}
