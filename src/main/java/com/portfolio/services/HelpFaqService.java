package com.portfolio.services;

import com.portfolio.dtos.Help.HelpFaqRequest;
import com.portfolio.dtos.Help.HelpFaqResponse;
import com.portfolio.exceptions.GenericException;

import java.util.List;

public interface HelpFaqService {
    List<HelpFaqResponse> getActiveFaqs();
    List<HelpFaqResponse> getAllFaqsForManagement();
    HelpFaqResponse createFaq(HelpFaqRequest req);
    HelpFaqResponse updateFaq(Long id, HelpFaqRequest req) throws GenericException;
    void deleteFaq(Long id) throws GenericException;
}
