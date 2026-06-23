package com.portfolio.dtos.SeoMeta;

import com.portfolio.enums.PageKeyEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SeoMetaResponseDTO {
    private Long id;
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
}
