package com.portfolio.servicesImpl;

import com.portfolio.dtos.SkillRequest;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.entities.Logo;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.LogoRepository;
import com.portfolio.repositories.SkillRepository;
import com.portfolio.services.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final LogoRepository logoRepository;

    @Override
    public SkillResponse create(SkillRequest request) throws GenericException {
        Logo logo = logoRepository.findById(request.getLogoId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Logo not found"));
        if (skillRepository.existsByLogoIdAndProfileId(request.getLogoId(), request.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Skill already exists for this profile");
        }
        Skill skill = Skill.builder()
                .logo(logo)
                .level(request.getLevel())
                .category(request.getCategory())
                .profileId(request.getProfileId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return mapToResponse(skillRepository.save(skill));
    }

    @Override
    public SkillResponse update(String id, SkillRequest request) throws GenericException {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        Logo logo = logoRepository.findById(request.getLogoId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Logo not found"));
        if (skillRepository.existsByLogoIdAndProfileId(request.getLogoId(), request.getProfileId()) && !skill.getLogo().getId().equals(request.getLogoId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Skill already exists for this profile");
        }
        skill.setLogo(logo);
        skill.setLevel(request.getLevel());
        skill.setCategory(request.getCategory());
        skill.setProfileId(request.getProfileId());
        skill.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(skillRepository.save(skill));
    }

    @Override
    public SkillResponse getById(String id) throws GenericException {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        return mapToResponse(skill);
    }

    @Override
    public void delete(String id) throws GenericException {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        skillRepository.delete(skill);
    }

    @Override
    public Page<SkillResponse> getByProfile(String profileId, Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasProfileId = profileId != null;
        boolean hasSearch = search != null && !search.isBlank();

        Page<Skill> skills;
        if( hasSearch && hasProfileId){
            skills = skillRepository.findByProfileIdWithSearch(profileId,search,sortedPageable);
        }else if(hasSearch){
            skills = skillRepository.findBySearch(search,sortedPageable);
        }else if(hasProfileId) {
            skills = skillRepository.findByProfileId(profileId, sortedPageable);
        }else{
            skills = skillRepository.findAll(sortedPageable);
        }
        return skills.map(this::mapToResponse);
    }

    @Override
    public List<SkillResponse> getByProfile(String profileId){
        return skillRepository.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .logoId(skill.getLogo().getId())
                .logoName(skill.getLogo().getName())
                .logoUrl(skill.getLogo().getUrl())
                .category(skill.getCategory())
                .level(skill.getLevel())
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }
}
