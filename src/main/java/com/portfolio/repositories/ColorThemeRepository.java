package com.portfolio.repositories;

import com.portfolio.entities.ColorTheme;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorThemeRepository extends MongoRepository<ColorTheme, String> {

    Optional<ColorTheme> findByThemeName(String themeName);

    Page<ColorTheme> findAll(Pageable pageable);

    Page<ColorTheme> findByStatus(StatusEnum status, Pageable pageable);

    @Query("""
    {
      $or: [
        { "themeName": { $regex: ?0, $options: "i" } }
      ]
    }
    """)
    Page<ColorTheme> searchByThemeName(String search, Pageable pageable);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "themeName": { $regex: ?0, $options: "i" } }
          ]
        },
        { "status": ?1 }
      ]
    }
    """)
    Page<ColorTheme> searchByThemeNameAndStatus(
            String search,
            StatusEnum status,
            Pageable pageable
    );

    Optional<ColorTheme> findFirstByStatusOrderByCreatedAtDesc(StatusEnum active);
}
