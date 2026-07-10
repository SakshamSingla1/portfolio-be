package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.BlogStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = false, exclude = "tags")

@Entity
@Table(
    name = "blog_posts",
    uniqueConstraints = @UniqueConstraint(name = "blog_posts_profile_slug_unique", columnNames = {"profile_id", "slug"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String excerpt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BlogStatusEnum status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "read_time_mins")
    private Integer readTimeMins;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "blog_post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<BlogTag> tags = new HashSet<>();
}
