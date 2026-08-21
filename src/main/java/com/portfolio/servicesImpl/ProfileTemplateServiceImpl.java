package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.ProfileTemplate.ProfileTemplateRequest;
import com.portfolio.dtos.ProfileTemplate.ProfileTemplateResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.TemplateKeyEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ProfileTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileTemplateServiceImpl implements ProfileTemplateService {

    private static final TemplateKeyEnum DEFAULT_TEMPLATE = TemplateKeyEnum.CLASSIC;

    private final ProfileDao profileDao;

    @Override
    public ProfileTemplateResponse getTemplateByProfileId(Long profileId) throws GenericException {
        Profile profile = findProfile(profileId);
        return mapToResponse(profile);
    }

    @Override
    public ProfileTemplateResponse setTemplateForProfile(Long profileId, ProfileTemplateRequest request) throws GenericException {
        Profile profile = findProfile(profileId);
        profile.setTemplateKey(request.getTemplateKey());
        return mapToResponse(profileDao.save(profile));
    }

    @Override
    public void resetTemplateForProfile(Long profileId) throws GenericException {
        Profile profile = findProfile(profileId);
        profile.setTemplateKey(DEFAULT_TEMPLATE);
        profileDao.save(profile);
    }

    @Override
    public long countProfilesByTemplateKey(String templateKey) {
        return profileDao.countByTemplateKey(TemplateKeyEnum.valueOf(templateKey));
    }

    private Profile findProfile(Long profileId) throws GenericException {
        return profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));
    }

    private ProfileTemplateResponse mapToResponse(Profile profile) {
        return ProfileTemplateResponse.builder()
                .profileId(profile.getId())
                .username(profile.getUserName())
                .templateKey(profile.getTemplateKey() != null ? profile.getTemplateKey().name() : DEFAULT_TEMPLATE.name())
                .build();
    }
}
