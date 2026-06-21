package com.portfolio.repositories;

import com.portfolio.entities.NavLink;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NavLinkRepository extends JpaRepository<NavLink, Long> {

    Page<NavLink> findByStatus(StatusEnum status, Pageable pageable);

    @Query("SELECT n FROM NavLink n WHERE LOWER(n.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.path) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<NavLink> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT n FROM NavLink n WHERE (LOWER(n.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.path) LIKE LOWER(CONCAT('%', :search, '%'))) AND n.status = :status")
    Page<NavLink> searchByStatus(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable
    );
}
