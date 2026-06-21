package com.portfolio.dtos.GitHub;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GitHubStatsDTO {
    private String username;
    private int publicRepos;
    private int followers;
    private int totalStars;
    private Integer externalPRs;
}
