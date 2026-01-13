package com.portfolio.entities;

import com.portfolio.enums.NotificationChannelEnum;
import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification-templates")
public class NotificationTemplate {
    @Id
    private String id;
    private String name;
    private String subject;
    private String body;
    private NotificationChannelEnum type;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
