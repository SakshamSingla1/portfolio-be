package com.portfolio.repositories;

import com.portfolio.dtos.User.UserResponse;
import com.portfolio.entities.Profile;
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
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByPhone(String phone);

    Optional<Profile> findByUserName(String userName);

    boolean existsByEmail(String newEmail);

    @Query("""
                SELECT NEW com.portfolio.dtos.User.UserResponse(
                    p.id, p.fullName, p.userName, p.email, p.roleId,
                    r.name, p.status, p.emailVerified, p.phoneVerified, 
                    fa.path, p.createdAt, p.updatedAt, p.createdBy, p.updatedBy,
                    p1.fullName, p2.fullName
                ) FROM Profile p
                    LEFT JOIN Role r ON p.roleId = r.id
                    LEFT JOIN Profile p1 ON p.createdBy = p1.id
                    LEFT JOIN Profile p2 ON p.updatedBy = p2.id
                    LEFT JOIN FileAsset fa ON fa.resourceId = p.id AND fa.resourceType = 'PROFILE'
                WHERE (:search IS NULL OR :search = '' OR 
                       LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                       LOWER(p.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                       LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND (:statuses IS NULL OR p.status IN :statuses)
                  AND (:roleIds IS NULL OR p.roleId IN :roleIds)
                    """)
    Page<UserResponse> findByCriteria(
            @Param("search") String search,
            @Param("statuses") List<StatusEnum> statuses,
            @Param("roleIds") List<Long> roleIds,
            Pageable pageable);

    @Query(value = """
            SELECT
              (SELECT COUNT(*) FROM skills         WHERE profile_id = :profileId) AS total_skills,
              (SELECT COUNT(*) FROM educations      WHERE profile_id = :profileId) AS total_education,
              (SELECT COUNT(*) FROM experiences     WHERE profile_id = :profileId) AS total_experience,
              (SELECT COUNT(*) FROM projects        WHERE profile_id = :profileId) AS total_projects,
              (SELECT COUNT(*) FROM achievements    WHERE profile_id = :profileId) AS total_achievements,
              (SELECT COUNT(*) FROM testimonials    WHERE profile_id = :profileId) AS total_testimonials,
              (SELECT COUNT(*) FROM certifications  WHERE profile_id = :profileId) AS total_certification,
              (SELECT COUNT(*) FROM contact_us      WHERE profile_id = :profileId) AS total_messages,
              (SELECT COUNT(*) FROM contact_us      WHERE profile_id = :profileId AND status = 'UNREAD') AS unread_messages,
              (SELECT COUNT(*) FROM social_links    WHERE profile_id = :profileId) AS total_social_links
            """, nativeQuery = true)
    List<Object[]> getDashboardStats(@Param("profileId") Long profileId);

    @Query(value = """
            SELECT type, description_label, ts, entity_id FROM (
              (SELECT 'SKILL' AS type,
                      CONCAT('Skill ', CASE WHEN s.created_at = s.updated_at THEN 'added' ELSE 'updated' END, ': ', COALESCE(s.logo_name, '')) AS description_label,
                      s.updated_at AS ts, CAST(s.id AS TEXT) AS entity_id
               FROM skills s WHERE s.profile_id = :profileId ORDER BY s.updated_at DESC LIMIT 1)
              UNION ALL
              (SELECT 'EDUCATION' AS type,
                      CONCAT('Education ', CASE WHEN e.created_at = e.updated_at THEN 'added' ELSE 'updated' END, ': ', COALESCE(e.degree, ''), ' at ', COALESCE(e.institution, '')) AS description_label,
                      e.updated_at AS ts, CAST(e.id AS TEXT) AS entity_id
               FROM educations e WHERE e.profile_id = :profileId ORDER BY e.updated_at DESC LIMIT 1)
              UNION ALL
              (SELECT 'EXPERIENCE' AS type,
                      CONCAT('Experience ', CASE WHEN ex.created_at = ex.updated_at THEN 'added' ELSE 'updated' END, ': ', COALESCE(ex.job_title, ''), ' at ', COALESCE(ex.company_name, '')) AS description_label,
                      ex.updated_at AS ts, CAST(ex.id AS TEXT) AS entity_id
               FROM experiences ex WHERE ex.profile_id = :profileId ORDER BY ex.updated_at DESC LIMIT 1)
              UNION ALL
              (SELECT 'PROJECT' AS type,
                      CONCAT('Project ', CASE WHEN p.created_at = p.updated_at THEN 'added' ELSE 'updated' END, ': ', COALESCE(p.project_name, '')) AS description_label,
                      p.updated_at AS ts, CAST(p.id AS TEXT) AS entity_id
               FROM projects p WHERE p.profile_id = :profileId ORDER BY p.updated_at DESC LIMIT 1)
              UNION ALL
              (SELECT 'ACHIEVEMENT' AS type,
                      CONCAT('Achievement ', CASE WHEN a.created_at = a.updated_at THEN 'added' ELSE 'updated' END, ': ', COALESCE(a.title, '')) AS description_label,
                      a.updated_at AS ts, CAST(a.id AS TEXT) AS entity_id
               FROM achievements a WHERE a.profile_id = :profileId ORDER BY a.updated_at DESC LIMIT 1)
              UNION ALL
              (SELECT 'CERTIFICATION' AS type,
                      CONCAT('Certification ', CASE WHEN c.created_at = c.updated_at THEN 'added' ELSE 'updated' END, ': ', COALESCE(c.title, '')) AS description_label,
                      c.updated_at AS ts, CAST(c.id AS TEXT) AS entity_id
               FROM certifications c WHERE c.profile_id = :profileId ORDER BY c.updated_at DESC LIMIT 1)
              UNION ALL
              (SELECT 'TESTIMONIAL' AS type,
                      CONCAT('Testimonial received from ', COALESCE(t.name, '')) AS description_label,
                      t.updated_at AS ts, CAST(t.id AS TEXT) AS entity_id
               FROM testimonials t WHERE t.profile_id = :profileId ORDER BY t.updated_at DESC LIMIT 1)
              UNION ALL
              (SELECT 'MESSAGE' AS type,
                      CONCAT('New message from ', COALESCE(cu.name, '')) AS description_label,
                      cu.created_at AS ts, CAST(cu.id AS TEXT) AS entity_id
               FROM contact_us cu WHERE cu.profile_id = :profileId ORDER BY cu.created_at DESC LIMIT 1)
            ) combined
            ORDER BY ts DESC NULLS LAST
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> getLatestActivities(@Param("profileId") Long profileId);
}

