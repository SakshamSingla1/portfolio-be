package com.portfolio.servicesImpl;

import com.portfolio.dao.blog.BlogPostDao;
import com.portfolio.dao.blog.BlogTagDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.File.FileAssetDTO;
import com.portfolio.dtos.Blog.BlogPostRequest;
import com.portfolio.dtos.Blog.BlogPostResponse;
import com.portfolio.dtos.Blog.BlogPostSummary;
import com.portfolio.dtos.Blog.BlogTagResponse;
import com.portfolio.dtos.File.FileUploadRequest;
import com.portfolio.dtos.Image.ImageUploadResponse;
import com.portfolio.entities.BlogPost;
import com.portfolio.entities.BlogTag;
import com.portfolio.enums.BlogStatusEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.BlogPostService;
import com.portfolio.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostDao blogPostDao;
    private final BlogTagDao blogTagDao;
    private final ProfileDao profileDao;
    private final FileService fileService;

    @Override
    @Transactional
    public BlogPostResponse create(BlogPostRequest request) throws GenericException {
        if (!profileDao.existsById(request.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found");
        }
        if (blogPostDao.existsByProfileIdAndSlug(request.getProfileId(), request.getSlug())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_BLOG_POST, "A post with this slug already exists");
        }
        Set<BlogTag> tags = resolveTags(request.getTagIds());
        BlogPost post = BlogPost.builder()
                .profileId(request.getProfileId())
                .title(request.getTitle())
                .slug(request.getSlug())
                .content(request.getContent())
                .excerpt(request.getExcerpt())
                .status(request.getStatus() != null ? request.getStatus() : BlogStatusEnum.DRAFT)
                .readTimeMins(request.getReadTimeMins())
                .viewCount(0)
                .tags(tags)
                .build();
        if (post.getStatus() == BlogStatusEnum.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }
        return mapToFullResponse(blogPostDao.save(post));
    }

    @Override
    @Transactional
    public BlogPostResponse update(Long id, BlogPostRequest request) throws GenericException {
        BlogPost post = blogPostDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.BLOG_POST_NOT_FOUND, "Blog post not found"));
        if (!post.getSlug().equals(request.getSlug()) &&
                blogPostDao.existsByProfileIdAndSlug(post.getProfileId(), request.getSlug())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_BLOG_POST, "A post with this slug already exists");
        }
        boolean beingPublished = post.getStatus() != BlogStatusEnum.PUBLISHED
                && request.getStatus() == BlogStatusEnum.PUBLISHED;

        post.setTitle(request.getTitle());
        post.setSlug(request.getSlug());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setStatus(request.getStatus());
        post.setReadTimeMins(request.getReadTimeMins());
        post.setTags(resolveTags(request.getTagIds()));
        if (beingPublished) {
            post.setPublishedAt(LocalDateTime.now());
        }
        return mapToFullResponse(blogPostDao.save(post));
    }

    @Override
    public BlogPostResponse getById(Long id) throws GenericException {
        return blogPostDao.findById(id)
                .map(this::mapToFullResponse)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.BLOG_POST_NOT_FOUND, "Blog post not found"));
    }

    @Override
    @Transactional
    public String delete(Long id) throws GenericException {
        if (!blogPostDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.BLOG_POST_NOT_FOUND, "Blog post not found");
        }
        try {
            fileService.deleteByResource(id, ResourceTypeEnum.BLOG_POST.name());
        } catch (Exception ignored) {}
        blogPostDao.deleteById(id);
        return "Blog post deleted successfully";
    }

    @Override
    public Page<BlogPostSummary> getByProfile(Long profileId, BlogStatusEnum status, String search,
                                              String sortBy, String sortDir, Pageable pageable) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return blogPostDao.findByCriteria(profileId, status, search, sorted).map(this::mapToSummary);
    }

    @Override
    @Transactional
    public BlogPostResponse publish(Long id) throws GenericException {
        BlogPost post = blogPostDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.BLOG_POST_NOT_FOUND, "Blog post not found"));
        if (post.getStatus() == BlogStatusEnum.PUBLISHED) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Blog post is already published");
        }
        post.setStatus(BlogStatusEnum.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
        return mapToFullResponse(blogPostDao.save(post));
    }

    @Override
    @Transactional
    public BlogPostResponse archive(Long id) throws GenericException {
        BlogPost post = blogPostDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.BLOG_POST_NOT_FOUND, "Blog post not found"));
        post.setStatus(BlogStatusEnum.ARCHIVED);
        return mapToFullResponse(blogPostDao.save(post));
    }

    @Override
    public ImageUploadResponse uploadCoverImage(Long postId, MultipartFile file) throws IOException, GenericException {
        blogPostDao.findById(postId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.BLOG_POST_NOT_FOUND, "Blog post not found"));
        FileUploadRequest uploadReq = new FileUploadRequest();
        uploadReq.setResourceId(postId);
        uploadReq.setResourceType(ResourceTypeEnum.BLOG_POST);
        uploadReq.setPrimary(true);
        try {
            var asset = fileService.upload(file, uploadReq);
            return new ImageUploadResponse(asset.getPath(), asset.getPublicId());
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Failed to upload cover image: " + e.getMessage());
        }
    }

    @Override
    public Page<BlogPostSummary> getPublishedByUsername(String username, String search,
                                                        String sortBy, String sortDir,
                                                        Pageable pageable) throws GenericException {
        var profile = profileDao.findByUserName(username)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "publishedAt");
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<BlogPost> posts = blogPostDao.findByCriteria(profile.getId(), BlogStatusEnum.PUBLISHED, search, sorted);

        List<Long> postIds = posts.stream().map(BlogPost::getId).toList();
        Map<Long, FileAssetDTO> coverMap = postIds.isEmpty()
                ? Map.of()
                : fileService.getPrimaryFilesForResources(postIds, ResourceTypeEnum.BLOG_POST);

        return posts.map(post -> mapToSummaryWithCover(post, coverMap.get(post.getId())));
    }

    @Override
    @Transactional
    public BlogPostResponse getPublishedByUsernameAndSlug(String username, String slug) throws GenericException {
        var profile = profileDao.findByUserName(username)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        BlogPost post = blogPostDao.findByProfileIdAndSlug(profile.getId(), slug)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.BLOG_POST_NOT_FOUND, "Blog post not found"));

        if (post.getStatus() != BlogStatusEnum.PUBLISHED) {
            throw new GenericException(ExceptionCodeEnum.BLOG_POST_NOT_FOUND, "Blog post not found");
        }

        blogPostDao.incrementViewCount(post.getId());
        return mapToFullResponse(post);
    }

    private Set<BlogTag> resolveTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(blogTagDao.findByIdIn(tagIds));
    }

    private String resolveCoverImageUrl(Long postId) {
        var asset = fileService.getPrimaryFile(postId, ResourceTypeEnum.BLOG_POST);
        return asset != null ? asset.getPath() : null;
    }

    private BlogPostResponse mapToFullResponse(BlogPost post) {
        return BlogPostResponse.builder()
                .id(post.getId())
                .profileId(post.getProfileId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .excerpt(post.getExcerpt())
                .status(post.getStatus())
                .publishedAt(post.getPublishedAt())
                .viewCount(post.getViewCount())
                .readTimeMins(post.getReadTimeMins())
                .coverImageUrl(resolveCoverImageUrl(post.getId()))
                .tags(mapTags(post.getTags()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private BlogPostSummary mapToSummary(BlogPost post) {
        return mapToSummaryWithCover(post,
                fileService.getPrimaryFile(post.getId(), ResourceTypeEnum.BLOG_POST));
    }

    private BlogPostSummary mapToSummaryWithCover(BlogPost post, FileAssetDTO cover) {
        return BlogPostSummary.builder()
                .id(post.getId())
                .profileId(post.getProfileId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .status(post.getStatus())
                .publishedAt(post.getPublishedAt())
                .viewCount(post.getViewCount())
                .readTimeMins(post.getReadTimeMins())
                .coverImageUrl(cover != null ? cover.getPath() : null)
                .tags(mapTags(post.getTags()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private List<BlogTagResponse> mapTags(Set<BlogTag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(t -> BlogTagResponse.builder().id(t.getId()).name(t.getName()).build())
                .toList();
    }
}
