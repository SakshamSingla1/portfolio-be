package com.portfolio.repositories;

import com.portfolio.entities.BlogTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogTagRepository extends JpaRepository<BlogTag, Long> {

    Optional<BlogTag> findByName(String name);

    boolean existsByName(String name);

    List<BlogTag> findByIdIn(List<Long> ids);

    @Query("""
            SELECT t FROM BlogTag t
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(t.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%'))
            ORDER BY t.name ASC
            """)
    Page<BlogTag> findByCriteria(@Param("search") String search, Pageable pageable);
}
