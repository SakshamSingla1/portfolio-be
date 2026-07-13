package com.portfolio.dtos.GitHub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubRepoResponse {
    private Long id;
    private String name;
    private String fullName;
    private String description;
    private String url;
    private String homepage;
    private String language;
    private int stars;
    private int forks;
    private boolean isPinned;
    private boolean isVisible;
    private int sortOrder;
}
