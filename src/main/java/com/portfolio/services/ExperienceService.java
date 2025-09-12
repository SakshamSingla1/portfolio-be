package com.portfolio.services;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Experience;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ExperienceRepository;
import com.portfolio.repositories.SkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ExperienceService(ExperienceRepository experienceRepository, SkillRepository skillRepository) {
        this.experienceRepository = experienceRepository;
        this.skillRepository = skillRepository;
    }

    public ExperienceResponse create(ExperienceRequest req) throws GenericException {
        try {
            Date parsedStartDate = sdf.parse(req.getStartDate());

            if (experienceRepository.existsByCompanyNameAndJobTitleAndStartDate(
                    req.getCompanyName(), req.getJobTitle(), parsedStartDate)) {
                return null;
            }

            List<Skill> skills = skillRepository.findAllById(req.getTechnologiesUsed());

            Experience exp = Experience.builder()
                    .companyName(req.getCompanyName())
                    .jobTitle(req.getJobTitle())
                    .location(req.getLocation())
                    .startDate(parsedStartDate)
                    .endDate(req.getEndDate() != null && !req.getEndDate().trim().isEmpty() ? sdf.parse(req.getEndDate()) : null)
                    .currentlyWorking(req.isCurrentlyWorking())
                    .description(req.getDescription())
                    .technologiesUsed(skills)
                    .build();

            Experience saved = experienceRepository.save(exp);
            return mapToResponse(saved);

        } catch (ParseException e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS,"Invalid date format", e.getMessage());
        }
    }

    public List<ExperienceResponse> getAll() {
        List<Experience> list = experienceRepository.findAll();
        List<ExperienceResponse> result = list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return result;
    }

    public ExperienceResponse getById(Integer id) throws GenericException {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND,"Experience not found"));

        return mapToResponse(experience);
    }

    public ExperienceResponse update(Integer id, ExperienceRequest req) throws GenericException {
        try {
            Experience exp = experienceRepository.findById(id)
                    .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND,"Experience not found"));

            List<Skill> skills = skillRepository.findAllById(req.getTechnologiesUsed());

            exp.setCompanyName(req.getCompanyName());
            exp.setJobTitle(req.getJobTitle());
            exp.setLocation(req.getLocation());
            exp.setStartDate(sdf.parse(req.getStartDate()));
            exp.setEndDate(req.getEndDate() != null && !req.getEndDate().trim().isEmpty() ? sdf.parse(req.getEndDate()) : null);
            exp.setCurrentlyWorking(req.isCurrentlyWorking());
            exp.setDescription(req.getDescription());
            exp.setTechnologiesUsed(skills);

            Experience updated = experienceRepository.save(exp);
            return mapToResponse(updated);

        } catch (ParseException e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_CREDENTIALS,"Invalid date format", e.getMessage());
        }
    }

    public String delete(Integer id) throws GenericException {
        if (!experienceRepository.existsById(id)) {
            return "Experience not found";
        }

        experienceRepository.deleteById(id);
        return "Experience deleted successfully";
    }

    private ExperienceResponse mapToResponse(Experience exp) {
        return ExperienceResponse.builder()
                .id(exp.getId())
                .companyName(exp.getCompanyName())
                .jobTitle(exp.getJobTitle())
                .location(exp.getLocation())
                .startDate(sdf.format(exp.getStartDate()))
                .endDate(exp.getEndDate() != null ? sdf.format(exp.getEndDate()) : null)
                .currentlyWorking(exp.isCurrentlyWorking())
                .description(exp.getDescription())
                .technologiesUsed(
                        exp.getTechnologiesUsed().stream()
                                .map(skill -> new SkillDropdown(
                                        skill.getId(),
                                        skill.getLogo().getName(),
                                        skill.getLogo().getUrl()
                                ))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
