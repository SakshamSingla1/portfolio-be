package com.portfolio.servicesImpl;

import com.portfolio.dao.experience.ExperienceDao;
import com.portfolio.dao.skill.SkillDao;
import com.portfolio.dtos.Experience.ExperienceRequest;
import com.portfolio.dtos.Experience.ExperienceResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Experience;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceDao experienceDao;
    private final SkillDao skillDao;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ExperienceResponse create(ExperienceRequest req) throws GenericException {
        LocalDate startDate = parseDate(req.getStartDate());
        if (experienceDao.existsByProfileIdAndCompanyNameAndJobTitleAndStartDate(req.getProfileId(), req.getCompanyName(), req.getJobTitle(), startDate)) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Duplicate experience exists");
        }
        Experience experience = Experience.builder()
                .profileId(req.getProfileId())
                .companyName(req.getCompanyName())
                .jobTitle(req.getJobTitle())
                .location(req.getLocation())
                .startDate(startDate)
                .endDate(parseOptionalDate(req.getEndDate()))
                .employmentStatus(req.getEmploymentStatus())
                .description(req.getDescription())
                .skillIds(req.getSkillIds())
                .build();
        return mapToResponse(experienceDao.save(experience));
    }

    @Override
    public ExperienceResponse update(Long id, ExperienceRequest req) throws GenericException {
        Experience experience = experienceDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found"));
        LocalDate startDate = parseDate(req.getStartDate());
        if (experienceDao.existsByProfileIdAndCompanyNameAndJobTitleAndStartDateAndIdNot(req.getProfileId(), req.getCompanyName(), req.getJobTitle(), startDate, id)) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Duplicate experience exists");
        }
        experience.setCompanyName(req.getCompanyName());
        experience.setJobTitle(req.getJobTitle());
        experience.setLocation(req.getLocation());
        experience.setStartDate(startDate);
        experience.setEndDate(parseOptionalDate(req.getEndDate()));
        experience.setEmploymentStatus(req.getEmploymentStatus());
        experience.setDescription(req.getDescription());
        experience.setSkillIds(req.getSkillIds());
        experience.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(experienceDao.save(experience));
    }

    @Override
    public ExperienceResponse getById(Long id) throws GenericException {
        Experience experience = experienceDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found"));
        return mapToResponse(experience);
    }

    @Override
    public String delete(Long id) throws GenericException {
        if (!experienceDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found");
        }
        experienceDao.deleteById(id);
        return "Experience deleted successfully";
    }

    @Override
    public Page<ExperienceResponse> getByProfile(Long profileId, String search, Pageable pageable) {
        return experienceDao.findByCriteria(profileId,search,pageable);
    }

    @Override
    public List<ExperienceResponse> getByProfile(Long profileId) {
        return experienceDao.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private LocalDate parseDate(String date) throws GenericException {
        try {
            return LocalDate.parse(date, FORMATTER);
        } catch (Exception e) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Invalid date format (yyyy-MM-dd)");
        }
    }

    private LocalDate parseOptionalDate(String date) {
        try {
            return (date == null || date.isBlank()) ? null : LocalDate.parse(date, FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private ExperienceResponse mapToResponse(Experience exp) {

        List<Long> skillIdLongs = exp.getSkillIds() == null ? List.of() : exp.getSkillIds().stream()
                .map(Long::valueOf)
                .toList();

        List<SkillDropdown> skills =
                skillIdLongs.isEmpty()
                        ? List.of()
                        : skillDao.findDropdownByIds(skillIdLongs);

        return ExperienceResponse.builder()
                .id(exp.getId())
                .companyName(exp.getCompanyName())
                .jobTitle(exp.getJobTitle())
                .location(exp.getLocation())
                .startDate(exp.getStartDate().format(FORMATTER))
                .endDate(exp.getEndDate() != null
                        ? exp.getEndDate().format(FORMATTER)
                        : null)
                .employmentStatus(exp.getEmploymentStatus())
                .description(exp.getDescription())
                .skills(skills)
                .createdAt(exp.getCreatedAt())
                .updatedAt(exp.getUpdatedAt())
                .build();
    }
}
