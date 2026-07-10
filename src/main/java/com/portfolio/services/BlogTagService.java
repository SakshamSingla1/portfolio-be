package com.portfolio.services;

import com.portfolio.dtos.Blog.BlogTagRequest;
import com.portfolio.dtos.Blog.BlogTagResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogTagService {

    BlogTagResponse create(BlogTagRequest request) throws GenericException;

    BlogTagResponse update(Long id, BlogTagRequest request) throws GenericException;

    BlogTagResponse getById(Long id) throws GenericException;

    String delete(Long id) throws GenericException;

    Page<BlogTagResponse> getAll(String search, Pageable pageable);
}
