package com.portfolio.dao.blog;

import com.portfolio.entities.BlogPost;
import com.portfolio.enums.BlogStatusEnum;
import com.portfolio.repositories.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BlogPostDao {

    private final BlogPostRepository blogPostRepository;

    public BlogPost save(BlogPost post) {
        return blogPostRepository.save(post);
    }

    public Optional<BlogPost> findById(Long id) {
        return blogPostRepository.findById(id);
    }

    public Optional<BlogPost> findByProfileIdAndSlug(Long profileId, String slug) {
        return blogPostRepository.findByProfileIdAndSlug(profileId, slug);
    }

    public boolean existsByProfileIdAndSlug(Long profileId, String slug) {
        return blogPostRepository.existsByProfileIdAndSlug(profileId, slug);
    }

    public boolean existsById(Long id) {
        return blogPostRepository.existsById(id);
    }

    public Page<BlogPost> findByCriteria(Long profileId, BlogStatusEnum status, String search, Pageable pageable) {
        return blogPostRepository.findByCriteria(profileId, status, search, pageable);
    }

    public long countByProfileId(Long profileId) {
        return blogPostRepository.countByProfileId(profileId);
    }

    public long countByProfileIdAndStatus(Long profileId, BlogStatusEnum status) {
        return blogPostRepository.countByProfileIdAndStatus(profileId, status);
    }

    public void incrementViewCount(Long id) {
        blogPostRepository.incrementViewCount(id);
    }

    public void deleteById(Long id) {
        blogPostRepository.deleteById(id);
    }
}
