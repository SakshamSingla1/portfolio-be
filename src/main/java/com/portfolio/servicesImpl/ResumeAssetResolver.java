package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.resume.ResumeDao;
import com.portfolio.dtos.Resume.ActiveResumeAssetDTO;
import com.portfolio.entities.FileAsset;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Resume;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

// Kept as its own bean (rather than a private method on ResumePublicServiceImpl) so @Cacheable
// is actually honored: Spring's caching proxy only intercepts calls that arrive through the bean
// reference, not a class's own internal self-invocations.
@Component
@RequiredArgsConstructor
public class ResumeAssetResolver {

    private final ProfileDao profileDao;
    private final ResumeDao resumeDao;
    private final FileAssetDao fileAssetDao;

    @Cacheable(cacheNames = "activeResumeAsset", key = "#username")
    public ActiveResumeAssetDTO resolve(String username) throws GenericException {
        Profile profile = profileDao.findByUserName(username)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        Resume resume = resumeDao.findByProfileIdAndStatus(profile.getId(), StatusEnum.ACTIVE)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.RESUME_NOT_FOUND, "Active resume not found"));

        FileAsset asset = fileAssetDao.findByResourceIdAndResourceTypeAndIsPrimaryTrue(resume.getId(), ResourceTypeEnum.RESUME)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.RESUME_NOT_FOUND, "Resume file not found"));

        String fileName = asset.getMetaData();
        if (fileName == null || fileName.isBlank()) {
            fileName = "resume.pdf";
        }

        return ActiveResumeAssetDTO.builder()
                .profileId(profile.getId())
                .resumeId(resume.getId())
                .path(asset.getPath())
                .fileName(fileName)
                .build();
    }
}
