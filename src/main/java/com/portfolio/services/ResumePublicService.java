package com.portfolio.services;

import com.portfolio.exceptions.GenericException;
import jakarta.servlet.http.HttpServletResponse;

public interface ResumePublicService {

    void viewResume(String username, HttpServletResponse response) throws GenericException;

    void downloadResume(String username, HttpServletResponse response) throws GenericException;
}
