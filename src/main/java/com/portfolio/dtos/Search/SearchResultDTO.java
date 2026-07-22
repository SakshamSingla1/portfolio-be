package com.portfolio.dtos.Search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    private String module;
    private Long id;
    private String title;
    private String snippet;
    private String path;
}
