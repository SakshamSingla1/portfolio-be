package com.portfolio.dtos.Resume;

import com.portfolio.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResumeUploadResponseDTO {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String publicId;
    private StatusEnum status;
    private LocalDateTime updatedAt;
}
