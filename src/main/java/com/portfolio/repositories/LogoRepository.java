package com.portfolio.repositories;

import com.portfolio.entities.Logo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoRepository extends JpaRepository<Logo, Long> {

    boolean existsByName(String name);

    @Query("SELECT l FROM Logo l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Logo> findByNameWithSearch(@Param("search") String search, Pageable pageable);
}
