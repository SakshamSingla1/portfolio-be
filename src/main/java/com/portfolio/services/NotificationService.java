package com.portfolio.services;

import com.portfolio.dtos.Notification.NotificationResponseDTO;
import com.portfolio.enums.NotificationTypeEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void create(Long profileId, NotificationTypeEnum type, String title, String message, String link);

    Page<NotificationResponseDTO> getByProfile(Long profileId, Pageable pageable);

    long getUnreadCount(Long profileId);

    void markAsRead(Long id, Long profileId) throws GenericException;

    void markAllAsRead(Long profileId);
}
