package com.portfolio.services;

import com.portfolio.dtos.GitHub.GitHubStatsDTO;

public interface GitHubService {
    GitHubStatsDTO fetchStats(String githubUrl);
}
