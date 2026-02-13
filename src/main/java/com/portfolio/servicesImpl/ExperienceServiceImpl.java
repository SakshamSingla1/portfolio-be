package com.portfolio.servicesImpl;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.entities.Experience;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ExperienceRepository;
import com.portfolio.repositories.SkillRepository;
import com.portfolio.services.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public ExperienceResponse create(ExperienceRequest req) throws GenericException {
        LocalDate startDate = parseDate(req.getStartDate());
        if (experienceRepository.existsByProfileIdAndCompanyNameAndJobTitleAndStartDate(req.getProfileId(), req.getCompanyName(), req.getJobTitle(), startDate)) {
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return mapToResponse(experienceRepository.save(experience));
    }

    @Override
    public ExperienceResponse update(String id, ExperienceRequest req) throws GenericException {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found"));
        LocalDate startDate = parseDate(req.getStartDate());
        if (experienceRepository.existsByProfileIdAndCompanyNameAndJobTitleAndStartDateAndIdNot(req.getProfileId(), req.getCompanyName(), req.getJobTitle(), startDate, id)) {
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
        return mapToResponse(experienceRepository.save(experience));
    }

    @Override
    public ExperienceResponse getById(String id) throws GenericException {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.EXPERIENCE_NOT_FOUND, "Experience not found"));
        return mapToResponse(experience);
    }

    @Override
    public String delete(String id) {
        experienceRepository.deleteById(id);
        return "Experience deleted successfully";
    }

    @Override
    public Page<ExperienceResponse> getByProfile(String profileId, String search, String sortDir, String sortBy,Pageable pageable) {
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

        Page<Experience> experiences;
        if( hasSearch && hasProfileId){
            experiences = experienceRepository.findByProfileIdWithSearch(profileId,search,sortedPageable);
        }else if(hasSearch){
            experiences = experienceRepository.findBySearch(search,sortedPageable);
        }else if(hasProfileId) {
            experiences = experienceRepository.findByProfileId(profileId, sortedPageable);
        }else{
            experiences = experienceRepository.findAll(sortedPageable);
        }
        return experiences.map(this::mapToResponse);
    }

    @Override
    public List<ExperienceResponse> getByProfile(String profileId) {
        return experienceRepository.findByProfileId(profileId)
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

        List<SkillDropdown> skills =
                exp.getSkillIds() == null
                        ? List.of()
                        : skillRepository.findAllById(exp.getSkillIds())
                        .stream()
                        .map(skill -> new SkillDropdown(
                                skill.getId(),
                                skill.getLogo().getName(),
                                skill.getLogo().getUrl()))
                        .toList();

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
