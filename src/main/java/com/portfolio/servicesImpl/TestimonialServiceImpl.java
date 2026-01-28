package com.portfolio.servicesImpl;

import com.portfolio.dtos.Testimonial.TestimonialRequestDTO;
import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.dtos.ImageUploadResponse;
import com.portfolio.entities.Testimonial;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.TestimonialRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.services.TestimonialService;
import com.portfolio.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final ProfileRepository profileRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public TestimonialResponseDTO createTestimonial(TestimonialRequestDTO dto) throws GenericException {
        if (testimonialRepository.existsByProfileIdAndOrder(dto.getProfileId(), dto.getOrder())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_TESTIMONIAL, "Testimonial already exists with same order for the profile");
        }
        Testimonial testimonial = Testimonial.builder()
                .profileId(dto.getProfileId())
                .name(dto.getName())
                .role(dto.getRole())
                .company(dto.getCompany())
                .message(dto.getMessage())
                .imageId(dto.getImageId())
                .imageUrl(dto.getImageUrl())
                .linkedInUrl(dto.getLinkedInUrl())
                .status(dto.getStatus())
                .order(dto.getOrder())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return mapToResponse(testimonialRepository.save(testimonial));
    }

    @Override
    public TestimonialResponseDTO updateTestimonial(String id, TestimonialRequestDTO dto) throws GenericException {
        Testimonial existing = testimonialRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TESTIMONIAL_NOT_FOUND, "Testimonial not found"));
        if (dto.getOrder() != null && testimonialRepository.existsByProfileIdAndOrderAndIdNot(existing.getProfileId(), dto.getOrder(), existing.getId())) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_TESTIMONIAL, "Testimonial already exists with same order for the profile");
        }
        existing.setName(dto.getName());
        existing.setRole(dto.getRole());
        existing.setCompany(dto.getCompany());
        existing.setMessage(dto.getMessage());
        existing.setImageId(dto.getImageId());
        existing.setImageUrl(dto.getImageUrl());
        existing.setLinkedInUrl(dto.getLinkedInUrl());
        existing.setStatus(dto.getStatus());
        existing.setOrder(dto.getOrder());
        existing.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(testimonialRepository.save(existing));
    }

    @Override
    public TestimonialResponseDTO getTestimonialById(String id) throws GenericException {
        Testimonial Testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TESTIMONIAL_NOT_FOUND, "Testimonial not found"));
        return mapToResponse(Testimonial);
    }

    @Override
    public Page<TestimonialResponseDTO> getByProfile(String profileId, String search, String sortDir, String sortBy, Pageable pageable) {
        String finalSortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "order";
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                finalSortBy
        );
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        Page<Testimonial> page;
        if (profileId != null && search != null && !search.isBlank()) {
            page = testimonialRepository.findByProfileIdWithSearch(profileId, search, sortedPageable);
        } else if (profileId != null) {
            page = testimonialRepository.findByProfileId(profileId, sortedPageable);
        } else if (search != null && !search.isBlank()) {
            page = testimonialRepository.findBySearch(search, sortedPageable);
        } else {
            page = testimonialRepository.findAll(sortedPageable);
        }
        return page.map(this::mapToResponse);
    }

    @Override
    public Void deleteById(String id) throws GenericException {
        if (!testimonialRepository.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.TESTIMONIAL_NOT_FOUND, "Testimonial not found");
        }
        testimonialRepository.deleteById(id);
        return null;
    }

    @Override
    public ImageUploadResponse uploadImage(
            String profileId,
            MultipartFile file
    ) throws GenericException, IOException {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
        Map uploadResult = cloudinaryService.uploadFile(file);
        return new ImageUploadResponse(uploadResult.get("secure_url").toString(), uploadResult.get("public_id").toString());
    }

    private TestimonialResponseDTO mapToResponse(Testimonial c) {
        return TestimonialResponseDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .role(c.getRole())
                .company(c.getCompany())
                .message(c.getMessage())
                .imageId(c.getImageId())
                .imageUrl(c.getImageUrl())
                .linkedInUrl(c.getLinkedInUrl())
                .status(c.getStatus())
                .order(c.getOrder())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
