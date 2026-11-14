package com.portfolio.services;

import com.portfolio.exceptions.GenericException;

public interface PortfolioExportService {
    /** Generates the resume PDF and returns its bytes directly -- the caller streams them straight
     * to the client rather than uploading to Cloudinary first and redirecting, which used to add a
     * full external network round-trip to every cold request. */
    byte[] exportPdf(String username) throws GenericException;
}
