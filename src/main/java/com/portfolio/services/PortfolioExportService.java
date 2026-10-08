package com.portfolio.services;

import com.portfolio.exceptions.GenericException;

public interface PortfolioExportService {
    /** Generates the PDF, uploads it to Cloudinary, and returns its URL (caller redirects to it). */
    String exportPdf(String username) throws GenericException;
}
