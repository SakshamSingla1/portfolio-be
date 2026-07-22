package com.portfolio.dao.notification;

import com.portfolio.entities.Notification;
import com.portfolio.repositories.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class NotificationDao {

    private final NotificationRepository notificationRepository;

    public NotificationDao(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Page<Notification> findByProfileIdOrderByCreatedAtDesc(Long profileId, Pageable pageable) {
        return notificationRepository.findByProfileIdOrderByCreatedAtDesc(profileId, pageable);
    }

    public long countByProfileIdAndIsReadFalse(Long profileId) {
        return notificationRepository.countByProfileIdAndIsReadFalse(profileId);
    }

    public Optional<Notification> findByIdAndProfileId(Long id, Long profileId) {
        return notificationRepository.findByIdAndProfileId(id, profileId);
    }

    public void markAllAsRead(Long profileId) {
        notificationRepository.markAllAsRead(profileId);
    }
}
