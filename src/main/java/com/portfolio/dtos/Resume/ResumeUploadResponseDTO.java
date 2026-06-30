package com.portfolio.dtos.Resume;

import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadResponseDTO {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String publicId;
    private StatusEnum status;
    private LocalDateTime updatedAt;
}
