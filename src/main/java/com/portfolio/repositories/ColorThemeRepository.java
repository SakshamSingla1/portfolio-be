package com.portfolio.repositories;

import com.portfolio.entities.ColorTheme;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorThemeRepository extends JpaRepository<ColorTheme, Long> {

    Optional<ColorTheme> findByThemeName(String themeName);

    Page<ColorTheme> findByStatus(StatusEnum status, Pageable pageable);

    @Query("SELECT c FROM ColorTheme c WHERE LOWER(c.themeName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ColorTheme> searchByThemeName(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM ColorTheme c WHERE LOWER(c.themeName) LIKE LOWER(CONCAT('%', :search, '%')) AND c.status = :status")
    Page<ColorTheme> searchByThemeNameAndStatus(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable
    );

    Optional<ColorTheme> findFirstByStatusOrderByCreatedAtDesc(StatusEnum status);
}
