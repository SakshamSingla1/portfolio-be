package com.portfolio.servicesImpl;

import com.portfolio.dao.notification.NotificationDao;
import com.portfolio.dtos.Notification.NotificationResponseDTO;
import com.portfolio.entities.Notification;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.NotificationTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;

    @Override
    @Transactional
    public void create(Long profileId, NotificationTypeEnum type, String title, String message, String link) {
        try {
            Notification notification = Notification.builder()
                    .profileId(profileId)
                    .type(type)
                    .title(title)
                    .message(message)
                    .link(link)
                    .isRead(false)
                    .build();
            notificationDao.save(notification);
        } catch (Exception e) {
            log.warn("Failed to create notification for profile {}: {}", profileId, e.getMessage());
        }
    }

    @Override
    public Page<NotificationResponseDTO> getByProfile(Long profileId, Pageable pageable) {
        return notificationDao.findByProfileIdOrderByCreatedAtDesc(profileId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public long getUnreadCount(Long profileId) {
        return notificationDao.countByProfileIdAndIsReadFalse(profileId);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long profileId) throws GenericException {
        Notification notification = notificationDao.findByIdAndProfileId(id, profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.NOTIFICATION_NOT_FOUND, "Notification not found"));
        notification.setRead(true);
        notificationDao.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long profileId) {
        notificationDao.markAllAsRead(profileId);
    }

    private NotificationResponseDTO mapToResponse(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .link(notification.getLink())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
