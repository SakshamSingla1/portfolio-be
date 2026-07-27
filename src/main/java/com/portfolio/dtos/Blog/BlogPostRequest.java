package com.portfolio.dtos.Blog;

import com.portfolio.enums.BlogStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlogPostRequest {

    // Not validated here: the controller derives this from the auth token and
    // overwrites it after @Valid has already run, so the client never sends it.
    private Long profileId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Slug is required")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    private String content;

    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    private String excerpt;

    @NotNull(message = "Status is required")
    private BlogStatusEnum status;

    private Integer readTimeMins;

    private List<Long> tagIds;
}
