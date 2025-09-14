package com.portfolio.services;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Experience;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ExperienceRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ExperienceService handles CRUD operations for user experiences.
 * Features:
 * - Create, update, get, delete experience
 * - Paginated search by profile with optional keyword
 * - Ensures uniqueness by company, job title, and start date
 * - Maps Experience entity to DTO including skills used
 */
@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final ProfileRepository profileRepository;

    private static final ThreadLocal<SimpleDateFormat> SDF =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    public ExperienceService(ExperienceRepository experienceRepository,
                             SkillRepository skillRepository,
                             ProfileRepository profileRepository) {
        this.experienceRepository = experienceRepository;
        this.skillRepository = skillRepository;
        this.profileRepository = profileRepository;
    }

    // ---------------- CREATE EXPERIENCE ----------------
    public ExperienceResponse create(ExperienceRequest req) throws GenericException {
        Profile profile = getProfile(req.getProfileId());
        Date startDate = parseDate(req.getStartDate());

        checkUniqueExperience(req, startDate, null);

        Experience exp = buildExperience(req, profile, startDate);
        return mapToResponse(experienceRepository.save(exp));
    }

    // ---------------- UPDATE EXPERIENCE ----------------
    public ExperienceResponse update(Integer id, ExperienceRequest req) throws GenericException {
        Experience exp = experienceRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found"));

        Date startDate = parseDate(req.getStartDate());
        checkUniqueExperience(req, startDate, id);

        updateExperience(exp, req, startDate);
        return mapToResponse(experienceRepository.save(exp));
    }

    // ---------------- GET EXPERIENCE BY ID ----------------
    public ExperienceResponse getById(Integer id) throws GenericException {
        Experience exp = experienceRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found"));
        return mapToResponse(exp);
    }

    // ---------------- DELETE EXPERIENCE ----------------
    public String delete(Integer id) throws GenericException {
        if (!experienceRepository.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found");
        }
        experienceRepository.deleteById(id);
        return "Experience deleted successfully";
    }

    // ---------------- LIST EXPERIENCES BY PROFILE WITH SEARCH ----------------
    public Page<ExperienceResponse> getExperienceByProfileId(Integer profileId, Pageable pageable, String search) {
        return experienceRepository.findByProfileIdWithSearch(profileId, search, pageable)
                .map(this::mapToResponse);
    }

    // ---------------- HELPER METHODS ----------------
    private Profile getProfile(Integer profileId) throws GenericException {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));
    }

    private void checkUniqueExperience(ExperienceRequest req, Date startDate, Integer excludeId) throws GenericException {
        boolean exists = (excludeId == null)
                ? experienceRepository.existsByProfileIdAndCompanyNameAndJobTitleAndStartDate(
                req.getProfileId(), req.getCompanyName(), req.getJobTitle(), startDate)
                : experienceRepository.existsByProfileIdAndCompanyNameAndJobTitleAndStartDateAndIdNot(
                req.getProfileId(), req.getCompanyName(), req.getJobTitle(), startDate, excludeId);

        if (exists) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT,
                    "Experience with same company, job title, and start date already exists");
        }
    }

    private Experience buildExperience(ExperienceRequest req, Profile profile, Date startDate) {
        List<Skill> skills = req.getTechnologiesUsed() != null
                ? skillRepository.findAllById(req.getTechnologiesUsed())
                : List.of();

        return Experience.builder()
                .companyName(req.getCompanyName())
                .jobTitle(req.getJobTitle())
                .location(req.getLocation())
                .startDate(startDate)
                .endDate(parseOptionalDate(req.getEndDate()))
                .currentlyWorking(Boolean.TRUE.equals(req.isCurrentlyWorking()))
                .description(req.getDescription())
                .technologiesUsed(skills)
                .profile(profile)
                .build();
    }

    private void updateExperience(Experience exp, ExperienceRequest req, Date startDate) {
        exp.setCompanyName(req.getCompanyName());
        exp.setJobTitle(req.getJobTitle());
        exp.setLocation(req.getLocation());
        exp.setStartDate(startDate);
        exp.setEndDate(parseOptionalDate(req.getEndDate()));
        exp.setCurrentlyWorking(Boolean.TRUE.equals(req.isCurrentlyWorking()));
        exp.setDescription(req.getDescription());
        exp.setTechnologiesUsed(req.getTechnologiesUsed() != null
                ? skillRepository.findAllById(req.getTechnologiesUsed())
                : List.of());
    }

    private Date parseDate(String date) throws GenericException {
        try {
            if (date == null || date.isBlank()) throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Date cannot be empty");
            return SDF.get().parse(date);
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Invalid date format. Use yyyy-MM-dd");
        }
    }

    private Date parseOptionalDate(String date) {
        try {
            return (date != null && !date.isBlank()) ? SDF.get().parse(date) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private ExperienceResponse mapToResponse(Experience exp) {
        return ExperienceResponse.builder()
                .id(exp.getId())
                .companyName(exp.getCompanyName())
                .jobTitle(exp.getJobTitle())
                .location(exp.getLocation())
                .startDate(SDF.get().format(exp.getStartDate()))
                .endDate(exp.getEndDate() != null ? SDF.get().format(exp.getEndDate()) : null)
                .currentlyWorking(Boolean.TRUE.equals(exp.isCurrentlyWorking()))
                .description(exp.getDescription())
                .technologiesUsed(exp.getTechnologiesUsed().stream()
                        .map(skill -> new SkillDropdown(skill.getId(), skill.getLogo().getName(), skill.getLogo().getUrl()))
                        .toList())
                .build();
    }
}
