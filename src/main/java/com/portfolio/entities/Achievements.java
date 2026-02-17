package com.portfolio.entities;

import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_profile_updated",
                def = "{ 'profileId': 1, 'updatedAt': -1 }"
        ),
        @CompoundIndex(
                name = "idx_profile_status_order",
                def = "{ 'profileId': 1, 'status': 1, 'order': 1 }"
        ),
        @CompoundIndex(
                name = "idx_profile_order_unique",
                def = "{ 'profileId': 1, 'order': 1 }",
                unique = true
        )
})
public class Achievements {
    @Id
    private String id;

    @Indexed
    private String profileId;

    @TextIndexed
    private String title;
    private String description;

    @TextIndexed
    private String issuer;
    private LocalDate achievedAt;
    private String proofUrl;
    private String proofPublicId;
    private String order;

    @Indexed
    private StatusEnum status;

    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

