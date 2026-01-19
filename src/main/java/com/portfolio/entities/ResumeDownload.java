package com.portfolio.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "resume_downloads")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDownload {
    @Id
    private String id;
    private String profileId;
    private String resumeId;
    private LocalDateTime downloadedAt;
}
