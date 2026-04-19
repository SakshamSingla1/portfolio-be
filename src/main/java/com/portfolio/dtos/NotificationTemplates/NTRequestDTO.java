package com.portfolio.dtos.NotificationTemplates;

import com.portfolio.enums.NotificationChannelEnum;
import com.portfolio.enums.StatusEnum;
import lombok.Data;

@Data
public class NTRequestDTO {
    private String name;
    private String subject;
    private String body;
    private NotificationChannelEnum type;
    private StatusEnum status;
}
