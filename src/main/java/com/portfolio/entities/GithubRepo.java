package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "github_repos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubRepo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "integration_id", nullable = false)
    private Long integrationId;

    @Column(name = "github_id", nullable = false)
    private Long githubId;

    @Column(nullable = false)
    private String name;

    @Column(name = "full_name")
    private String fullName;

    private String description;
    private String url;
    private String homepage;
    private String language;
    private int stars;
    private int forks;

    @Column(name = "is_pinned")
    private boolean isPinned;

    @Column(name = "is_visible")
    private boolean isVisible;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;
}
