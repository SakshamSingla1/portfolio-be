package com.portfolio.services;

import com.portfolio.dtos.Blog.BlogPostRequest;
import com.portfolio.dtos.Blog.BlogPostResponse;
import com.portfolio.dtos.Blog.BlogPostSummary;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.enums.BlogStatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BlogPostService {

    BlogPostResponse create(BlogPostRequest request) throws GenericException;

    BlogPostResponse update(Long id, BlogPostRequest request) throws GenericException;

    BlogPostResponse getById(Long id) throws GenericException;

    String delete(Long id) throws GenericException;

    Page<BlogPostSummary> getByProfile(Long profileId, BlogStatusEnum status, String search, String sortBy, String sortDir, Pageable pageable);

    BlogPostResponse publish(Long id) throws GenericException;

    BlogPostResponse archive(Long id) throws GenericException;

    ImageUploadResponse uploadCoverImage(Long postId, MultipartFile file) throws IOException, GenericException;

    // Public (no-auth) endpoints
    Page<BlogPostSummary> getPublishedByUsername(String username, String search, String sortBy, String sortDir, Pageable pageable) throws GenericException;

    BlogPostResponse getPublishedByUsernameAndSlug(String username, String slug) throws GenericException;
}
