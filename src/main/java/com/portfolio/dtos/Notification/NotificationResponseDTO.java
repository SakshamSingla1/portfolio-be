package com.portfolio.dtos.Notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.portfolio.enums.NotificationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private NotificationTypeEnum type;
    private String title;
    private String message;
    private String link;
    private boolean isRead;
    private LocalDateTime createdAt;

    @JsonProperty("isRead")
    public boolean isRead() {
        return isRead;
    }
}
