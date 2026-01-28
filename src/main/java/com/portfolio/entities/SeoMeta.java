package com.portfolio.entities;

import com.portfolio.enums.PageKeyEnum;
import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "seo-meta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoMeta {
    @Id
    private String id;
    private String profileId;
    private PageKeyEnum pageKey;
    private String title;
    private String description;
    private List<String> keywords;
    private String ogTitle;
    private String ogDescription;
    private String ogImageUrl;
    private String canonicalUrl;
    private Boolean indexable;
    private Boolean followLinks;
    private StatusEnum status;
    private LocalDateTime updatedAt;

}
