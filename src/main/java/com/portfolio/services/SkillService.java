package com.portfolio.services;

import com.portfolio.dtos.ProjectResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.dtos.SkillRequest;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.entities.Logo;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Project;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.LogoRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final LogoRepository logoRepository;
    private final ProfileRepository profileRepository;

    public SkillService(SkillRepository skillRepository, LogoRepository logoRepository, ProfileRepository profileRepository) {
        this.skillRepository = skillRepository;
        this.logoRepository = logoRepository;
        this.profileRepository = profileRepository;
    }

    // Fetch skills by profile with search and pagination
    public Page<SkillResponse> getSkillByProfileId(Integer profileId, Pageable pageable, String search) {
        return skillRepository.findByProfileIdWithSearch(profileId, search, pageable);
    }

    // Create a new skill
    public SkillResponse create(SkillRequest request) throws GenericException {
        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));

        Logo logo = logoRepository.findById(request.getLogoId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Logo not found"));

        if (skillRepository.existsByLogoIdAndProfileId(logo.getId(), profile.getId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Skill already exists for this profile");
        }

        Skill skill = Skill.builder()
                .logo(logo)
                .level(request.getLevel())
                .profile(profile)
                .build();

        return mapToResponse(skillRepository.save(skill));
    }

    // Update existing skill
    public SkillResponse update(Integer id, SkillRequest request) throws GenericException {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));

        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));

        Logo logo = logoRepository.findById(request.getLogoId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Logo not found"));

        if (skillRepository.existsByLogoIdAndProfileId(logo.getId(), profile.getId())
                && !skill.getLogo().getId().equals(logo.getId())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Skill already exists for this profile");
        }

        skill.setLogo(logo);
        skill.setLevel(request.getLevel());
        skill.setProfile(profile);

        return mapToResponse(skillRepository.save(skill));
    }

    // Fetch skill by id
    public SkillResponse getById(Integer id) throws GenericException {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        return mapToResponse(skill);
    }

    // Delete skill
    public void delete(Integer id) throws GenericException {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        skillRepository.delete(skill);
    }

    // ---------------- GET SkillS BY PROFILE ----------------
    public List<SkillResponse> getSkillByProfileId(Integer profileId) {
        List<Skill> skills = skillRepository.findByProfileId(profileId);
        return skills.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Map Skill entity to SkillResponse DTO
    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .logoId(skill.getLogo().getId())
                .logoName(skill.getLogo().getName())
                .logoUrl(skill.getLogo().getUrl())
                .level(skill.getLevel())
                .category(skill.getLogo().getCategory())
                .build();
    }
}
