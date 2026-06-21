package com.portfolio.repositories;

import com.portfolio.entities.NotificationTemplate;
import com.portfolio.enums.NotificationChannelEnum;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NTRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByName(String name);

    boolean existsByNameAndType(String name, NotificationChannelEnum type);

    Page<NotificationTemplate> findByStatus(StatusEnum status, Pageable pageable);

    @Query("SELECT n FROM NotificationTemplate n WHERE LOWER(n.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<NotificationTemplate> SearchByText(@Param("search") String search, Pageable pageable);

    @Query("SELECT n FROM NotificationTemplate n WHERE LOWER(n.name) LIKE LOWER(CONCAT('%', :search, '%')) AND n.status = :status")
    Page<NotificationTemplate> searchByStatusAndSearch(
            @Param("search") String search,
            @Param("status") StatusEnum status,
            Pageable pageable
    );
}
