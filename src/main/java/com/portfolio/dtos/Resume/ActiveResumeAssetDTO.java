package com.portfolio.dtos.Resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveResumeAssetDTO {
    private Long profileId;
    private Long resumeId;
    private String path;
    private String fileName;
}
