package com.portfolio.servicesImpl;

import com.portfolio.entities.FileAsset;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Resume;
import com.portfolio.entities.ResumeDownload;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.repositories.FileAssetRepository;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.ResumeDownloadRepository;
import com.portfolio.repositories.ResumeRepository;
import com.portfolio.services.ResumePublicService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumePublicServiceImpl implements ResumePublicService {

    private final ProfileRepository profileRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeDownloadRepository resumeDownloadRepository;
    private final FileAssetRepository fileAssetRepository;

    @Override
    public void viewResume(String username, HttpServletResponse response) throws GenericException {
        Resume resume = getActiveResume(username);
        FileAsset asset = fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(resume.getId()), ResourceTypeEnum.RESUME)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.RESUME_NOT_FOUND, "Resume file not found"));
        streamPdf(asset.getPath(), response, false);
    }

    @Override
    public void downloadResume(String username, HttpServletResponse response) throws GenericException {
        Resume resume = getActiveResume(username);
        FileAsset asset = fileAssetRepository.findByResourceIdAndResourceTypeAndIsPrimaryTrue(String.valueOf(resume.getId()), ResourceTypeEnum.RESUME)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.RESUME_NOT_FOUND, "Resume file not found"));
        String fileName = asset.getMetaData();
        if (fileName == null || fileName.isBlank()) {
            fileName = "resume";
        }
        streamPdf(asset.getPath(), response, true, fileName);
        resumeDownloadRepository.save(ResumeDownload.builder()
                .profileId(resume.getProfileId())
                .resumeId(resume.getId())
                .downloadedAt(LocalDateTime.now())
                .build());
    }

    // ================= PRIVATE =================

    private Resume getActiveResume(String username) throws GenericException {
        Profile profile = profileRepository.findByUserName(username)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.PROFILE_NOT_FOUND,
                        "Profile not found"
                ));

        return resumeRepository
                .findByProfileIdAndStatus(profile.getId(), StatusEnum.ACTIVE)
                .orElseThrow(() -> new GenericException(
                        ExceptionCodeEnum.RESUME_NOT_FOUND,
                        "Active resume not found"
                ));
    }

    private void streamPdf(String fileUrl, HttpServletResponse response, boolean download) throws GenericException {
        streamPdf(fileUrl, response, download, "resume.pdf");
    }

    private void streamPdf(
            String fileUrl,
            HttpServletResponse response,
            boolean download,
            String fileName
    ) throws GenericException {
        try (InputStream in = new URL(fileUrl).openStream()) {

            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    download
                            ? "attachment; filename=\"" + fileName + "\""
                            : "inline"
            );

            in.transferTo(response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            throw new GenericException(
                    ExceptionCodeEnum.FILE_STREAM_FAILED,
                    "Unable to stream resume"
            );
        }
    }
}
