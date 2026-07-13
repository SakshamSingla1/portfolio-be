package com.portfolio.controllers;

import com.portfolio.utils.Helper;
import com.portfolio.dtos.GitHub.GithubIntegrationResponse;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.GithubIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("api/v1/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubIntegrationService githubIntegrationService;
    private final Helper helper;

    @Value("${github.oauth.admin-ui-url:http://localhost:5174/github-integration}")
    private String adminUiUrl;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/oauth/url")
    public ResponseEntity<ResponseModel<Map<String, String>>> getOAuthUrl(Authentication auth) {
        Long profileId = helper.getProfileIdFromHeader(auth);
        String url = githubIntegrationService.getOAuthUrl(profileId);
        return ApiResponse.respond(Map.of("url", url), "OAuth URL generated", "Failed to generate OAuth URL");
    }

    // No auth — this is a redirect from GitHub
    @GetMapping("/oauth/callback")
    public ResponseEntity<Void> callback(@RequestParam String code, @RequestParam String state) {
        githubIntegrationService.handleCallback(code, state);
        return ResponseEntity.status(302).location(URI.create(adminUiUrl + "?connected=true")).build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/integration")
    public ResponseEntity<ResponseModel<GithubIntegrationResponse>> getIntegration(Authentication auth) {
        Long profileId = helper.getProfileIdFromHeader(auth);
        return ApiResponse.respond(githubIntegrationService.getIntegration(profileId),
                "Integration fetched", "Failed to fetch integration");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/sync")
    public ResponseEntity<ResponseModel<Void>> sync(Authentication auth) {
        Long profileId = helper.getProfileIdFromHeader(auth);
        githubIntegrationService.syncRepos(profileId);
        return ApiResponse.respond(null, "Sync completed", "Sync failed");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/integration")
    public ResponseEntity<ResponseModel<Void>> disconnect(Authentication auth) {
        Long profileId = helper.getProfileIdFromHeader(auth);
        githubIntegrationService.disconnect(profileId);
        return ApiResponse.respond(null, "Disconnected", "Failed to disconnect");
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/repos/{repoId}")
    public ResponseEntity<ResponseModel<Void>> updateRepo(
            @PathVariable Long repoId,
            @RequestParam(required = false) Boolean isVisible,
            @RequestParam(required = false) Integer sortOrder) {
        githubIntegrationService.updateRepo(repoId, isVisible, sortOrder);
        return ApiResponse.respond(null, "Repo updated", "Failed to update repo");
    }

}
