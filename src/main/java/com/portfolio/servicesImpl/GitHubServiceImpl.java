package com.portfolio.servicesImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dtos.GitHub.GitHubStatsDTO;
import com.portfolio.services.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubServiceImpl implements GitHubService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${github.token:}")
    private String githubToken;

    @Override
    public GitHubStatsDTO fetchStats(String githubUrl) {
        try {
            String username = extractUsername(githubUrl);
            if (username == null || username.isBlank()) return null;

            HttpHeaders headers = buildHeaders();

            JsonNode user = get("https://api.github.com/users/" + username, headers);
            JsonNode repos = get(
                    "https://api.github.com/users/" + username + "/repos?sort=stars&per_page=100&type=owner",
                    headers
            );
            JsonNode prs = get(
                    "https://api.github.com/search/issues?q=author:" + username
                            + "+type:pr+is:merged+-user:" + username + "&per_page=1",
                    headers
            );

            int publicRepos = user != null ? user.path("public_repos").asInt(0) : 0;
            int followers   = user != null ? user.path("followers").asInt(0) : 0;

            int totalStars = 0;
            if (repos != null && repos.isArray()) {
                for (JsonNode repo : repos) {
                    if (!repo.path("fork").asBoolean(false)) {
                        totalStars += repo.path("stargazers_count").asInt(0);
                    }
                }
            }

            Integer externalPRs = null;
            if (prs != null && prs.has("total_count")) {
                externalPRs = prs.path("total_count").asInt(0);
            }

            return GitHubStatsDTO.builder()
                    .username(username)
                    .publicRepos(publicRepos)
                    .followers(followers)
                    .totalStars(totalStars)
                    .externalPRs(externalPRs)
                    .build();

        } catch (Exception e) {
            log.warn("Failed to fetch GitHub stats for {}: {}", githubUrl, e.getMessage());
            return null;
        }
    }

    private JsonNode get(String url, HttpHeaders headers) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readTree(response.getBody());
            }
        } catch (Exception e) {
            log.warn("GitHub API call failed for {}: {}", url, e.getMessage());
        }
        return null;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        if (githubToken != null && !githubToken.isBlank()) {
            headers.set("Authorization", "Bearer " + githubToken);
        }
        return headers;
    }

    private String extractUsername(String url) {
        if (url == null) return null;
        return url.replaceAll("(?i)^https?://(www\\.)?github\\.com/?", "")
                  .replaceAll("/+$", "")
                  .split("/")[0]
                  .trim();
    }
}
