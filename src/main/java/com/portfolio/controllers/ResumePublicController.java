package com.portfolio.controllers;

import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ResumePublicService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/resume")
@RequiredArgsConstructor
public class ResumePublicController {

    private final ResumePublicService resumePublicService;

    @GetMapping("/view/{username}")
    public void viewResume(
            @PathVariable String username,
            HttpServletResponse response
    ) throws GenericException {
        resumePublicService.viewResume(username, response);
    }

    @GetMapping("/download/{username}")
    public void downloadResume(
            @PathVariable String username,
            HttpServletResponse response
    ) throws GenericException {
        resumePublicService.downloadResume(username, response);
    }
}
