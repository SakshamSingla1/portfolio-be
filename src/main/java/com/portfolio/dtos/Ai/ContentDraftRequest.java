package com.portfolio.dtos.Ai;

import com.portfolio.enums.ContentDraftTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContentDraftRequest {

    @NotNull
    private ContentDraftTypeEnum contentType;

    // Rough, unpolished notes from the user -- what the draft should be built from, not the
    // final text itself.
    @NotBlank
    @Size(max = 1000)
    private String prompt;
}
