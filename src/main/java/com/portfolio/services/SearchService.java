package com.portfolio.services;

import com.portfolio.dtos.Search.SearchResultDTO;

import java.util.List;

public interface SearchService {
    List<SearchResultDTO> search(Long profileId, String query);
}
