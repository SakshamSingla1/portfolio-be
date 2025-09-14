package com.portfolio.repositories;

import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.entities.Logo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoRepository extends JpaRepository<Logo, Integer> {

    @Query("""
        SELECT new com.portfolio.dtos.logo.LogoDropdown(l.id, l.name, l.url ,l.category)
        FROM Logo l
        WHERE (:search IS NULL OR :search = '' 
               OR LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<LogoDropdown> findAllWithPagination(String search, Pageable pageable);
}
