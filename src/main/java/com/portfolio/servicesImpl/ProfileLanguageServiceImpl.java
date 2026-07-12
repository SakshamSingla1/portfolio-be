package com.portfolio.servicesImpl;

import com.portfolio.dao.language.ProfileLanguageDao;
import com.portfolio.dtos.Language.ProfileLanguageRequest;
import com.portfolio.dtos.Language.ProfileLanguageResponse;
import com.portfolio.entities.ProfileLanguage;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ProfileLanguageService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileLanguageServiceImpl implements ProfileLanguageService {

    private final ProfileLanguageDao profileLanguageDao;
    private final Helper helper;

    @Override
    public ProfileLanguageResponse create(ProfileLanguageRequest req) throws GenericException {
        ProfileLanguage language = ProfileLanguage.builder()
                .profileId(req.getProfileId())
                .languageName(req.getLanguageName())
                .proficiency(req.getProficiency())
                .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
                .build();
        return mapToResponse(profileLanguageDao.save(language));
    }

    @Override
    public ProfileLanguageResponse update(Long id, ProfileLanguageRequest req) throws GenericException {
        ProfileLanguage existing = profileLanguageDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_LANGUAGE_NOT_FOUND, "Language not found"));
        existing.setLanguageName(req.getLanguageName());
        existing.setProficiency(req.getProficiency());
        if (req.getSortOrder() != null) existing.setSortOrder(req.getSortOrder());
        return mapToResponse(profileLanguageDao.save(existing));
    }

    @Override
    public ProfileLanguageResponse getById(Long id) throws GenericException {
        return mapToResponse(profileLanguageDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_LANGUAGE_NOT_FOUND, "Language not found")));
    }

    @Override
    public Page<ProfileLanguageResponse> getAll(Long profileId, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return profileLanguageDao
                    .findByProfileIdAndLanguageNameContainingIgnoreCase(profileId, search, pageable)
                    .map(this::mapToResponse);
        }
        return profileLanguageDao.findByProfileId(profileId, pageable).map(this::mapToResponse);
    }

    @Override
    public Void delete(Long id) throws GenericException {
        if (!profileLanguageDao.existsById(id)) {
            throw new GenericException(ExceptionCodeEnum.PROFILE_LANGUAGE_NOT_FOUND, "Language not found");
        }
        profileLanguageDao.deleteById(id);
        return null;
    }

    @Override
    public List<ProfileLanguageResponse> getByProfile(Long profileId) {
        return profileLanguageDao.findByProfileIdOrderBySortOrderAsc(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProfileLanguageResponse mapToResponse(ProfileLanguage language) {
        ProfileLanguageResponse response = ProfileLanguageResponse.builder()
                .id(language.getId())
                .languageName(language.getLanguageName())
                .proficiency(language.getProficiency())
                .sortOrder(language.getSortOrder())
                .build();
        helper.setAudit(language, response);
        return response;
    }
}
