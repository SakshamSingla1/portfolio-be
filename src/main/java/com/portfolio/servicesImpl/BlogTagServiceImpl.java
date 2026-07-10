package com.portfolio.servicesImpl;

import com.portfolio.dao.blog.BlogTagDao;
import com.portfolio.dtos.Blog.BlogTagRequest;
import com.portfolio.dtos.Blog.BlogTagResponse;
import com.portfolio.entities.BlogTag;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.BlogTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogTagServiceImpl implements BlogTagService {

    private final BlogTagDao blogTagDao;

    @Override
    public BlogTagResponse create(BlogTagRequest request) throws GenericException {
        if (blogTagDao.existsByName(request.getName())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_BLOG_TAG, "Tag with this name already exists");
        }
        BlogTag tag = BlogTag.builder()
                .name(request.getName().trim().toLowerCase())
                .build();
        return mapToResponse(blogTagDao.save(tag));
    }

    @Override
    public BlogTagResponse update(Long id, BlogTagRequest request) throws GenericException {
        BlogTag tag = blogTagDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.BLOG_TAG_NOT_FOUND, "Tag not found"));
        String newName = request.getName().trim().toLowerCase();
        if (!tag.getName().equals(newName) && blogTagDao.existsByName(newName)) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_BLOG_TAG, "Tag with this name already exists");
        }
        tag.setName(newName);
        return mapToResponse(blogTagDao.save(tag));
    }

    @Override
    public BlogTagResponse getById(Long id) throws GenericException {
        return blogTagDao.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.BLOG_TAG_NOT_FOUND, "Tag not found"));
    }

    @Override
    public String delete(Long id) throws GenericException {
        if (!blogTagDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.BLOG_TAG_NOT_FOUND, "Tag not found");
        }
        blogTagDao.deleteById(id);
        return "Tag deleted successfully";
    }

    @Override
    public Page<BlogTagResponse> getAll(String search, Pageable pageable) {
        return blogTagDao.findByCriteria(search, pageable).map(this::mapToResponse);
    }

    private BlogTagResponse mapToResponse(BlogTag tag) {
        return BlogTagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
