package com.portfolio.entities;

import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "social_links")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_profile_status_order",
                def = "{ 'profileId': 1, 'status': 1, 'order': 1 }"
        )
})

public class SocialLinks {
    @Id
    private String id;
    @Indexed
    private String profileId;
    @Indexed
    private PlatformEnum platform;
    private String url;
    private String order;
    @Indexed
    private StatusEnum status;
    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
