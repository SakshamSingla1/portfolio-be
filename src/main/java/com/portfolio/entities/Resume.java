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

import java.time.LocalDateTime;

@Document(collection = "resumes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_profile_status_created",
                def = "{ 'profileId': 1, 'status': 1, 'createdAt': -1 }"
        )
})
public class Resume {
    @Id
    private String id;
    @Indexed
    private String profileId;
    @TextIndexed
    private String fileName;
    private String fileUrl;
    private String publicId;
    @Indexed
    private StatusEnum status;
    @Indexed
    private LocalDateTime updatedAt;
}
