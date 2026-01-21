package com.portfolio.entities;

import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "social_links")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialLinks {
    @Id
    private String id;
    private String profileId;
    private PlatformEnum platform;
    private String url;
    private String order;
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
