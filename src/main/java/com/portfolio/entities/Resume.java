package com.portfolio.entities;

import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "resumes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Resume {
    @Id
    private String id;
    private String profileId;
    private String fileName;
    private String fileUrl;
    private String publicId;
    private StatusEnum status;
    private LocalDateTime uploadedAt;
}
