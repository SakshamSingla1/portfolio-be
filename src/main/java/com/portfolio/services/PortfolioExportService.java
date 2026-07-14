package com.portfolio.services;

import com.portfolio.exceptions.GenericException;

public interface PortfolioExportService {
    byte[] exportPdf(String username) throws GenericException;
}
