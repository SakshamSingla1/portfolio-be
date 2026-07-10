package com.portfolio.dtos.Blog;

import com.portfolio.enums.BlogStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BlogPostResponse {

    private Long id;
    private Long profileId;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private BlogStatusEnum status;
    private LocalDateTime publishedAt;
    private int viewCount;
    private Integer readTimeMins;
    private String coverImageUrl;
    private List<BlogTagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
