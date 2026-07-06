package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.converters.StringListConverter;
import com.portfolio.enums.PageKeyEnum;
import com.portfolio.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "seo_meta")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoMeta extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id")
    private Long profileId;

    @Enumerated(EnumType.STRING)
    private PageKeyEnum pageKey;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> keywords;

    private String ogTitle;

    @Column(columnDefinition = "TEXT")
    private String ogDescription;

    private String ogImageUrl;
    private String canonicalUrl;
    @Column(nullable = false)
    private boolean indexable;

    @Column(nullable = false)
    private boolean followLinks;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

}
