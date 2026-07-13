package com.portfolio.dtos.GitHub;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GithubIntegrationResponse {
    private Long id;
    private String githubUsername;
    private boolean isActive;
    private LocalDateTime lastSyncedAt;
    private int cachedPublicRepos;
    private int cachedFollowers;
    private int cachedTotalStars;
    private Integer cachedExternalPrs;
    private List<GithubRepoResponse> repos;
}
