package com.portfolio.services;

import com.portfolio.dtos.GitHub.GithubIntegrationResponse;
import com.portfolio.dtos.GitHub.GithubRepoResponse;
import com.portfolio.dtos.GitHub.GitHubStatsDTO;
import com.portfolio.exceptions.GenericException;

import java.util.List;
import java.util.Optional;

public interface GithubIntegrationService {
    String getOAuthUrl(Long profileId);
    void handleCallback(String code, String state);
    GithubIntegrationResponse getIntegration(Long profileId);
    void syncRepos(Long profileId);
    void disconnect(Long profileId);
    void updateRepo(Long repoId, Boolean isVisible, Integer sortOrder, Long profileId) throws GenericException;
    Optional<GitHubStatsDTO> getCachedStats(Long profileId);
    List<GithubRepoResponse> getVisibleRepos(Long profileId);
}
