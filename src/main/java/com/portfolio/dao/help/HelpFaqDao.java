package com.portfolio.dao.help;

import com.portfolio.entities.HelpFaq;
import com.portfolio.repositories.HelpFaqRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class HelpFaqDao {

    private final HelpFaqRepository helpFaqRepository;

    public HelpFaqDao(HelpFaqRepository helpFaqRepository) {
        this.helpFaqRepository = helpFaqRepository;
    }

    public HelpFaq save(HelpFaq faq) {
        return helpFaqRepository.save(faq);
    }

    public Optional<HelpFaq> findById(Long id) {
        return helpFaqRepository.findById(id);
    }

    public List<HelpFaq> findAll() {
        return helpFaqRepository.findAll();
    }

    public List<HelpFaq> findByIsActiveTrueOrderBySortOrderAsc() {
        return helpFaqRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public void delete(HelpFaq faq) {
        helpFaqRepository.delete(faq);
    }
}
