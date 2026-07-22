package com.portfolio.repositories;

import com.portfolio.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByProfileIdOrderByCreatedAtDesc(Long profileId, Pageable pageable);

    long countByProfileIdAndIsReadFalse(Long profileId);

    Optional<Notification> findByIdAndProfileId(Long id, Long profileId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.profileId = :profileId AND n.isRead = false")
    void markAllAsRead(@Param("profileId") Long profileId);
}
