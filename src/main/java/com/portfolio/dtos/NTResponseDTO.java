package com.portfolio.dtos;

import com.portfolio.enums.NotificationChannelEnum;
import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NTResponseDTO {
    private String id;
    private String name;
    private String subject;
    private String body;
    private NotificationChannelEnum type;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
