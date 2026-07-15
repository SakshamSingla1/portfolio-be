package com.portfolio.servicesImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dao.github.GithubIntegrationDao;
import com.portfolio.dao.github.GithubRepoDao;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.dtos.GitHub.GithubIntegrationResponse;
import com.portfolio.dtos.GitHub.GithubRepoResponse;
import com.portfolio.dtos.GitHub.GitHubStatsDTO;
import com.portfolio.entities.GithubIntegration;
import com.portfolio.entities.GithubRepo;
import com.portfolio.services.GithubIntegrationService;
import com.portfolio.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubIntegrationServiceImpl implements GithubIntegrationService {

    private final GithubIntegrationDao integrationDao;
    private final GithubRepoDao repoDao;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final EncryptionUtil encryptionUtil;

    @Value("${github.oauth.client-id:}")
    private String clientId;

    @Value("${github.oauth.client-secret:}")
    private String clientSecret;

    @Value("${github.oauth.redirect-uri:http://localhost:8080/api/v1/github/oauth/callback}")
    private String redirectUri;

    @Value("${github.oauth.admin-ui-url:http://localhost:5174/github-integration}")
    private String adminUiUrl;

    @Value("${github.oauth.state-secret:change-me-in-production}")
    private String stateSecret;

    // ── OAuth ────────────────────────────────────────────────────────────────

    @Override
    public String getOAuthUrl(Long profileId) {
        String state = buildState(profileId);
        return "https://github.com/login/oauth/authorize" +
                "?client_id=" + encode(clientId) +
                "&redirect_uri=" + encode(redirectUri) +
                "&scope=read%3Auser+repo" +
                "&state=" + encode(state);
    }

    @Override
    @Transactional
    public void handleCallback(String code, String state) {
        Long profileId = validateState(state);

        String accessToken = exchangeCodeForToken(code);
        if (accessToken == null) throw new RuntimeException("GitHub token exchange failed");

        String username = fetchUsername(accessToken);
        if (username == null) throw new RuntimeException("Could not fetch GitHub username");

        String encrypted = encryptionUtil.encrypt(accessToken);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        GithubIntegration integration = integrationDao.findByProfileId(profileId)
                .orElse(GithubIntegration.builder().profileId(profileId).createdAt(now).build());

        integration.setGithubUsername(username);
        integration.setAccessToken(encrypted);
        integration.setActive(true);
        integration.setUpdatedAt(now);
        integrationDao.save(integration);

        // Sync in background after saving
        Long savedProfileId = profileId;
        CompletableFuture.runAsync(() -> {
            try { syncRepos(savedProfileId); }
            catch (Exception e) { log.warn("Background sync failed for profile {}: {}", savedProfileId, e.getMessage()); }
        });
    }

    // ── Sync ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void syncRepos(Long profileId) {
        GithubIntegration integration = integrationDao.findByProfileId(profileId).orElseThrow();
        String token    = encryptionUtil.decrypt(integration.getAccessToken());
        String username = integration.getGithubUsername();
        HttpHeaders hdrs = buildAuthHeaders(token);

        // User stats
        JsonNode user = get("https://api.github.com/user", hdrs);
        if (user != null) {
            integration.setCachedPublicRepos(user.path("public_repos").asInt(0));
            integration.setCachedFollowers(user.path("followers").asInt(0));
        }

        // All repos (paginated)
        List<JsonNode> allRepos = new ArrayList<>();
        for (int page = 1; ; page++) {
            JsonNode batch = get(
                "https://api.github.com/user/repos?type=owner&per_page=100&sort=stars&page=" + page, hdrs);
            if (batch == null || !batch.isArray() || batch.size() == 0) break;
            for (JsonNode r : batch) allRepos.add(r);
            if (batch.size() < 100) break;
        }

        // Pinned repo IDs via GraphQL
        Set<Long> pinnedIds = fetchPinnedRepoIds(username, token);

        // Upsert — pinned repos get lower sort_order
        int totalStars = 0;
        int sortOrder  = 0;
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Pinned first
        for (JsonNode r : allRepos) {
            long ghId = r.path("id").asLong();
            if (pinnedIds.contains(ghId)) {
                upsertRepo(integration, r, true, sortOrder++, now);
                totalStars += r.path("stargazers_count").asInt(0);
            }
        }
        // Then the rest
        Set<Long> seenPinned = new HashSet<>(pinnedIds);
        for (JsonNode r : allRepos) {
            long ghId = r.path("id").asLong();
            if (!seenPinned.contains(ghId) && !r.path("fork").asBoolean(false)) {
                upsertRepo(integration, r, false, sortOrder++, now);
                totalStars += r.path("stargazers_count").asInt(0);
            }
        }

        // External PRs (best-effort)
        try {
            JsonNode prs = get(
                "https://api.github.com/search/issues?q=author:" + username +
                "+type:pr+is:merged+-user:" + username + "&per_page=1", hdrs);
            if (prs != null && prs.has("total_count")) {
                integration.setCachedExternalPrs(prs.path("total_count").asInt(0));
            }
        } catch (Exception e) {
            log.debug("External PRs query failed: {}", e.getMessage());
        }

        integration.setCachedTotalStars(totalStars);
        integration.setLastSyncedAt(now);
        integration.setUpdatedAt(now);
        integrationDao.save(integration);
    }

    private void upsertRepo(GithubIntegration integration, JsonNode r, boolean pinned, int sortOrder, LocalDateTime now) {
        long ghId = r.path("id").asLong();
        GithubRepo repo = repoDao.findByIntegrationIdAndGithubId(integration.getId(), ghId)
                .orElse(GithubRepo.builder()
                        .integrationId(integration.getId())
                        .githubId(ghId)
                        .isVisible(true)
                        .sortOrder(sortOrder)
                        .build());

        repo.setName(r.path("name").asText(""));
        repo.setFullName(r.path("full_name").asText(null));
        repo.setDescription(r.path("description").asText(null));
        repo.setUrl(r.path("html_url").asText(null));
        repo.setHomepage(r.path("homepage").asText(null));
        repo.setLanguage(r.path("language").asText(null));
        repo.setStars(r.path("stargazers_count").asInt(0));
        repo.setForks(r.path("forks_count").asInt(0));
        repo.setPinned(pinned);
        repo.setSyncedAt(now);
        // Only overwrite sortOrder for new repos; admin may have reordered existing ones
        if (repo.getId() == null) repo.setSortOrder(sortOrder);
        repoDao.save(repo);
    }

    private Set<Long> fetchPinnedRepoIds(String username, String token) {
        try {
            String query = "{ \"query\": \"{ user(login: \\\"" + username + "\\\") { pinnedItems(first: 6, types: REPOSITORY) { nodes { ... on Repository { databaseId } } } } }\" }";
            HttpHeaders hdrs = buildAuthHeaders(token);
            hdrs.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> response = restTemplate.exchange(
                "https://api.github.com/graphql",
                HttpMethod.POST,
                new HttpEntity<>(query, hdrs),
                String.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode nodes = root.path("data").path("user").path("pinnedItems").path("nodes");
                Set<Long> ids = new HashSet<>();
                if (nodes.isArray()) {
                    for (JsonNode n : nodes) {
                        long dbId = n.path("databaseId").asLong(0);
                        if (dbId > 0) ids.add(dbId);
                    }
                }
                return ids;
            }
        } catch (Exception e) {
            log.debug("Pinned repos GraphQL failed: {}", e.getMessage());
        }
        return Collections.emptySet();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    public GithubIntegrationResponse getIntegration(Long profileId) {
        return integrationDao.findByProfileId(profileId)
                .map(i -> {
                    List<GithubRepoResponse> repos = repoDao
                            .findByIntegrationIdOrderBySortOrderAscStarsDesc(i.getId())
                            .stream().map(this::toRepoResponse).toList();
                    return toIntegrationResponse(i, repos);
                })
                .orElse(null);
    }

    @Override
    public Optional<GitHubStatsDTO> getCachedStats(Long profileId) {
        return integrationDao.findByProfileId(profileId)
                .filter(GithubIntegration::isActive)
                .map(i -> GitHubStatsDTO.builder()
                        .username(i.getGithubUsername())
                        .publicRepos(i.getCachedPublicRepos())
                        .followers(i.getCachedFollowers())
                        .totalStars(i.getCachedTotalStars())
                        .externalPRs(i.getCachedExternalPrs())
                        .build());
    }

    @Override
    public List<GithubRepoResponse> getVisibleRepos(Long profileId) {
        return integrationDao.findByProfileId(profileId)
                .filter(GithubIntegration::isActive)
                .map(i -> repoDao
                        .findByIntegrationIdAndIsVisibleTrueOrderBySortOrderAscStarsDesc(i.getId())
                        .stream().map(this::toRepoResponse).toList())
                .orElse(Collections.emptyList());
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void disconnect(Long profileId) {
        integrationDao.findByProfileId(profileId).ifPresent(i -> {
            repoDao.deleteByIntegrationId(i.getId());
            integrationDao.delete(i);
        });
    }

    @Override
    public void updateRepo(Long repoId, Boolean isVisible, Integer sortOrder, Long profileId) throws GenericException {
        GithubRepo repo = repoDao.findById(repoId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.DATA_NOT_FOUND, "Repo not found"));
        GithubIntegration integration = integrationDao.findById(repo.getIntegrationId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.DATA_NOT_FOUND, "Integration not found"));
        if (!integration.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.FORBIDDEN, "You do not have permission to update this repo");
        }
        if (isVisible != null) repo.setVisible(isVisible);
        if (sortOrder != null) repo.setSortOrder(sortOrder);
        repoDao.save(repo);
    }

    // ── OAuth helpers ─────────────────────────────────────────────────────────

    private String buildState(Long profileId) {
        try {
            String payload = Base64.getUrlEncoder().encodeToString(
                (profileId + ":" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
            String sig = hmac(payload, stateSecret);
            return payload + "." + sig;
        } catch (Exception e) {
            throw new RuntimeException("State build failed", e);
        }
    }

    private Long validateState(String state) {
        try {
            int dot = state.lastIndexOf('.');
            if (dot < 0) throw new RuntimeException("Invalid state");
            String payload = state.substring(0, dot);
            String sig     = state.substring(dot + 1);
            if (!sig.equals(hmac(payload, stateSecret))) throw new RuntimeException("State HMAC mismatch");
            String decoded = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            long ts = Long.parseLong(parts[1]);
            if (System.currentTimeMillis() - ts > 10 * 60 * 1000L) throw new RuntimeException("State expired");
            return Long.parseLong(parts[0]);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("State validation failed", e);
        }
    }

    private String hmac(String data, String secret) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    // ── GitHub API helpers ────────────────────────────────────────────────────

    private String exchangeCodeForToken(String code) {
        try {
            HttpHeaders hdrs = new HttpHeaders();
            hdrs.set("Accept", "application/json");
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("code", code);
            body.add("redirect_uri", redirectUri);
            ResponseEntity<String> resp = restTemplate.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST, new HttpEntity<>(body, hdrs), String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                JsonNode node = objectMapper.readTree(resp.getBody());
                return node.path("access_token").asText(null);
            }
        } catch (Exception e) {
            log.error("Token exchange error: {}", e.getMessage());
        }
        return null;
    }

    private String fetchUsername(String token) {
        try {
            JsonNode user = get("https://api.github.com/user", buildAuthHeaders(token));
            return user != null ? user.path("login").asText(null) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private JsonNode get(String url, HttpHeaders headers) {
        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return objectMapper.readTree(resp.getBody());
            }
        } catch (Exception e) {
            log.debug("GitHub GET {} failed: {}", url, e.getMessage());
        }
        return null;
    }

    private HttpHeaders buildAuthHeaders(String token) {
        HttpHeaders h = new HttpHeaders();
        h.set("Accept", "application/vnd.github+json");
        h.set("X-GitHub-Api-Version", "2022-11-28");
        h.set("Authorization", "Bearer " + token);
        return h;
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private GithubRepoResponse toRepoResponse(GithubRepo r) {
        return GithubRepoResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .fullName(r.getFullName())
                .description(r.getDescription())
                .url(r.getUrl())
                .homepage(r.getHomepage())
                .language(r.getLanguage())
                .stars(r.getStars())
                .forks(r.getForks())
                .isPinned(r.isPinned())
                .isVisible(r.isVisible())
                .sortOrder(r.getSortOrder())
                .build();
    }

    private GithubIntegrationResponse toIntegrationResponse(GithubIntegration i, List<GithubRepoResponse> repos) {
        return GithubIntegrationResponse.builder()
                .id(i.getId())
                .githubUsername(i.getGithubUsername())
                .isActive(i.isActive())
                .lastSyncedAt(i.getLastSyncedAt())
                .cachedPublicRepos(i.getCachedPublicRepos())
                .cachedFollowers(i.getCachedFollowers())
                .cachedTotalStars(i.getCachedTotalStars())
                .cachedExternalPrs(i.getCachedExternalPrs())
                .repos(repos)
                .build();
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
