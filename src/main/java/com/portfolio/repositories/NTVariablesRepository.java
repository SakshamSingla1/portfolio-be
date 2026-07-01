package com.portfolio.repositories;

import com.portfolio.dtos.NotificationTemplates.NotificationTemplateVariablesListResponseDTO;
import com.portfolio.entities.NotificationTemplateVariables;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NTVariablesRepository extends JpaRepository<NotificationTemplateVariables, Long> {

    @Query(value = """
        SELECT new com.portfolio.dtos.NotificationTemplates.NotificationTemplateVariablesListResponseDTO(
            ntv.id, ntv.variableName, ntv.htmlContent, ntv.whatsappVariable, ntv.templateId,
            ntv.createdAt, ntv.updatedAt)
        FROM NotificationTemplateVariables ntv
        WHERE ntv.createdAt = (
            SELECT MAX(sub.createdAt)
            FROM NotificationTemplateVariables sub
            WHERE sub.variableName = ntv.variableName
        )
        AND (:search = '' OR LOWER(ntv.variableName) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
        """,
        countQuery = """
        SELECT COUNT(ntv)
        FROM NotificationTemplateVariables ntv
        WHERE ntv.createdAt = (
            SELECT MAX(sub.createdAt)
            FROM NotificationTemplateVariables sub
            WHERE sub.variableName = ntv.variableName
        )
        AND (:search = '' OR LOWER(ntv.variableName) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
        """)
    Page<NotificationTemplateVariablesListResponseDTO> findByCriteria(
            @Param("search") String search,
            Pageable pageable);
}
