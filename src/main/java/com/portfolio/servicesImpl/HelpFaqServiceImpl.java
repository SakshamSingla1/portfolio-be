package com.portfolio.servicesImpl;

import com.portfolio.dao.help.HelpFaqDao;
import com.portfolio.dtos.Help.HelpFaqRequest;
import com.portfolio.dtos.Help.HelpFaqResponse;
import com.portfolio.entities.HelpFaq;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.HelpFaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpFaqServiceImpl implements HelpFaqService {

    private final HelpFaqDao helpFaqDao;

    @Override
    public List<HelpFaqResponse> getActiveFaqs() {
        return helpFaqDao.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<HelpFaqResponse> getAllFaqsForManagement() {
        return helpFaqDao.findAll().stream()
                .sorted(Comparator.comparingInt(HelpFaq::getSortOrder))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public HelpFaqResponse createFaq(HelpFaqRequest req) {
        HelpFaq faq = HelpFaq.builder()
                .question(req.getQuestion())
                .answer(req.getAnswer())
                .sortOrder(req.getSortOrder())
                .isActive(req.isActive())
                .build();
        return mapToResponse(helpFaqDao.save(faq));
    }

    @Override
    public HelpFaqResponse updateFaq(Long id, HelpFaqRequest req) throws GenericException {
        HelpFaq faq = helpFaqDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.HELP_FAQ_NOT_FOUND, "FAQ not found"));

        faq.setQuestion(req.getQuestion());
        faq.setAnswer(req.getAnswer());
        faq.setSortOrder(req.getSortOrder());
        faq.setActive(req.isActive());

        return mapToResponse(helpFaqDao.save(faq));
    }

    @Override
    public void deleteFaq(Long id) throws GenericException {
        HelpFaq faq = helpFaqDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.HELP_FAQ_NOT_FOUND, "FAQ not found"));
        helpFaqDao.delete(faq);
    }

    private HelpFaqResponse mapToResponse(HelpFaq f) {
        return HelpFaqResponse.builder()
                .id(f.getId())
                .question(f.getQuestion())
                .answer(f.getAnswer())
                .sortOrder(f.getSortOrder())
                .isActive(f.isActive())
                .build();
    }
}
