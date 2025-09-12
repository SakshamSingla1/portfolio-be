package com.portfolio.services;

import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.dtos.SkillRequest;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.entities.Logo;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.LogoRepository;
import com.portfolio.repositories.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final LogoRepository logoRepository;

    public SkillService(SkillRepository skillRepository, LogoRepository logoRepository) {
        this.skillRepository = skillRepository;
        this.logoRepository = logoRepository;
    }

    public Page<SkillDropdown> getAllSkillsByPage(Pageable pageable, String search) {
        return skillRepository.findAllWithPagination(search, pageable);
    }

    public SkillResponse create(SkillRequest request) throws GenericException {
        Logo logo = logoRepository.findById(request.getLogoId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Logo not found"));

        Skill skill = Skill.builder()
                .logo(logo)
                .level(request.getLevel())
                .build();

        Skill saved = skillRepository.save(skill);
        return mapToResponse(saved);
    }

    public SkillResponse update(Integer id, SkillRequest request) throws GenericException {
        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));

        Logo logo = logoRepository.findById(request.getLogoId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Logo not found"));

        existingSkill.setLogo(logo);
        existingSkill.setLevel(request.getLevel());

        Skill saved = skillRepository.save(existingSkill);
        return mapToResponse(saved);
    }

    public List<SkillResponse> getAll() {
        List<SkillResponse> response = skillRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return response;
    }

    public SkillResponse getById(Integer id) throws GenericException {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        return mapToResponse(skill);
    }

    public String delete(Integer id) throws GenericException {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.SKILL_NOT_FOUND, "Skill not found"));
        skillRepository.delete(skill);
        return "Skill deleted successfully";
    }

    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .logoName(skill.getLogo().getName())
                .logoUrl(skill.getLogo().getUrl())
                .level(skill.getLevel())
                .category(skill.getLogo().getCategory())
                .build();
    }
}
