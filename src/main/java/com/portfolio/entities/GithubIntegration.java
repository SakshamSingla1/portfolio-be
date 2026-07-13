package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "github_integrations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false, unique = true)
    private Long profileId;

    @Column(name = "github_username", nullable = false)
    private String githubUsername;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "token_scope")
    private String tokenScope;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "cached_public_repos")
    private int cachedPublicRepos;

    @Column(name = "cached_followers")
    private int cachedFollowers;

    @Column(name = "cached_total_stars")
    private int cachedTotalStars;

    @Column(name = "cached_external_prs")
    private Integer cachedExternalPrs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
