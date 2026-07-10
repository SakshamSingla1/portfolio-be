package com.portfolio.dao.blog;

import com.portfolio.entities.BlogTag;
import com.portfolio.repositories.BlogTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BlogTagDao {

    private final BlogTagRepository blogTagRepository;

    public BlogTag save(BlogTag tag) {
        return blogTagRepository.save(tag);
    }

    public Optional<BlogTag> findById(Long id) {
        return blogTagRepository.findById(id);
    }

    public Optional<BlogTag> findByName(String name) {
        return blogTagRepository.findByName(name);
    }

    public boolean existsByName(String name) {
        return blogTagRepository.existsByName(name);
    }

    public boolean existsById(Long id) {
        return blogTagRepository.existsById(id);
    }

    public List<BlogTag> findByIdIn(List<Long> ids) {
        return blogTagRepository.findByIdIn(ids);
    }

    public Page<BlogTag> findByCriteria(String search, Pageable pageable) {
        return blogTagRepository.findByCriteria(search, pageable);
    }

    public void deleteById(Long id) {
        blogTagRepository.deleteById(id);
    }
}
