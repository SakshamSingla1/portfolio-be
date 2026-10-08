package com.portfolio.servicesImpl;

import com.portfolio.dao.resume.ResumeDownloadDao;
import com.portfolio.dtos.Resume.ActiveResumeAssetDTO;
import com.portfolio.entities.ResumeDownload;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ResumePublicService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class ResumePublicServiceImpl implements ResumePublicService {

    private final ResumeAssetResolver resumeAssetResolver;
    private final ResumeDownloadDao resumeDownloadDao;
    private final ExecutorService profileAggregationExecutor;

    @Override
    public void viewResume(String username, HttpServletResponse response) throws GenericException {
        ActiveResumeAssetDTO asset = resumeAssetResolver.resolve(username);
        redirectTo(asset.getPath(), response);
    }

    @Override
    public void downloadResume(String username, HttpServletResponse response) throws GenericException {
        ActiveResumeAssetDTO asset = resumeAssetResolver.resolve(username);
        // Fire-and-forget: the client shouldn't wait on this analytics write to get redirected.
        CompletableFuture.runAsync(() -> resumeDownloadDao.save(ResumeDownload.builder()
                .profileId(asset.getProfileId())
                .resumeId(asset.getResumeId())
                .downloadedAt(LocalDateTime.now())
                .build()), profileAggregationExecutor);
        redirectTo(withAttachmentFlag(asset.getPath(), asset.getFileName()), response);
    }

    // ================= PRIVATE =================

    // The file already lives on Cloudinary's CDN as a public URL, so the browser fetches it
    // directly from there instead of this server proxying the bytes through itself (was slow —
    // a blocking double hop with no read timeout — and left corrupted partial downloads on any
    // mid-stream failure, since response headers/bytes were already committed by then).
    private void redirectTo(String url, HttpServletResponse response) throws GenericException {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new GenericException(
                    ExceptionCodeEnum.FILE_STREAM_FAILED,
                    "Unable to redirect to resume"
            );
        }
    }

    // Cloudinary serves raw/upload URLs with a forced attachment Content-Disposition (and a
    // chosen filename) when an "fl_attachment:<filename>" flag is inserted right after "/upload/".
    private String withAttachmentFlag(String secureUrl, String fileName) {
        String marker = "/upload/";
        int idx = secureUrl.indexOf(marker);
        if (idx < 0) return secureUrl;
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        int afterMarker = idx + marker.length();
        return secureUrl.substring(0, afterMarker) + "fl_attachment:" + encodedName + "/" + secureUrl.substring(afterMarker);
    }
}
