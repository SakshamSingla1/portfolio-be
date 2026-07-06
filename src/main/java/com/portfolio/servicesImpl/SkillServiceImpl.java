package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.logo.LogoDao;
import com.portfolio.dao.skill.SkillDao;
import com.portfolio.dtos.Skill.SkillRequest;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.dtos.Skill.SkillStat;
import com.portfolio.entities.FileAsset;
import com.portfolio.entities.Logo;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.enums.SkillLevelEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillDao skillDao;
    private final LogoDao logoDao;
    private final FileAssetDao fileAssetDao;

    @Override
    public SkillResponse create(SkillRequest request) throws GenericException {
        Logo logo = logoDao.findById(request.getLogoId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Logo not found"));
        if (skillDao.existsByLogoIdAndProfileId(request.getLogoId(), request.getProfileId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Skill already exists for this profile");
        }
        Skill skill = Skill.builder()
                .logo(logo)
                .level(request.getLevel())
                .category(request.getCategory())
                .profileId(request.getProfileId())
                .build();
        return mapToResponse(skillDao.save(skill));
    }

    @Override
    public SkillResponse update(Long id, SkillRequest request) throws GenericException {
        Skill skill = skillDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        Logo logo = logoDao.findById(request.getLogoId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Logo not found"));
        if (skillDao.existsByLogoIdAndProfileId(request.getLogoId(), request.getProfileId()) && !skill.getLogo().getId().equals(request.getLogoId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Skill already exists for this profile");
        }
        skill.setLogo(logo);
        skill.setLevel(request.getLevel());
        skill.setCategory(request.getCategory());
        skill.setProfileId(request.getProfileId());
        return mapToResponse(skillDao.save(skill));
    }

    @Override
    public SkillResponse getById(Long id) throws GenericException {
        return skillDao.findDTOById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
    }

    @Override
    public void delete(Long id) throws GenericException {
        Skill skill = skillDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        skillDao.delete(skill);
    }

    @Override
    public Page<SkillResponse> getByProfile(Long profileId, Pageable pageable, String search, String sortDir, String sortBy) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        return skillDao.findByCriteria(profileId, search, sortedPageable);
    }

    @Override
    public List<SkillResponse> getByProfile(Long profileId){
        return skillDao.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SkillStat getStats() {
        return SkillStat.builder()
                .expertSkillCount(skillDao.countByLevel(SkillLevelEnum.Expert))
                .advancedSkillCount(skillDao.countByLevel(SkillLevelEnum.Advanced))
                .intermediateSkillCount(skillDao.countByLevel(SkillLevelEnum.Intermediate))
                .beginnerSkillCount(skillDao.countByLevel(SkillLevelEnum.Beginner))
                .build();
    }

    private SkillResponse mapToResponse(Skill skill) {
        String logoUrl = fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(skill.getLogoId(), ResourceTypeEnum.LOGO)
                .map(FileAsset::getPath)
                .orElse(null);
        return SkillResponse.builder()
                .id(skill.getId())
                .logoId(skill.getLogoId())
                .logoName(skill.getLogoName())
                .logoUrl(logoUrl)
                .category(skill.getCategory())
                .level(skill.getLevel())
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }
}
