package com.portfolio.dao.notification_template;

import com.portfolio.dtos.NotificationTemplates.NotificationTemplateListResponseDTO;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.repositories.NTRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class NTDao {

    private final NTRepository ntRepository;

    public NTDao(NTRepository ntRepository) {
        this.ntRepository = ntRepository;
    }

    public NotificationTemplate save(NotificationTemplate template) {
        return ntRepository.save(template);
    }

    public Optional<NotificationTemplate> findById(Long id) {
        return ntRepository.findById(id);
    }

    public void deleteById(Long id) {
        ntRepository.deleteById(id);
    }

    public Optional<NotificationTemplate> findByTemplate(String template) {
        return ntRepository.findByTemplate(template);
    }

    public Page<NotificationTemplateListResponseDTO> findByCriteria(String search, Pageable pageable) {
        return ntRepository.findByCriteria(search, pageable);
    }

    public Page<NotificationTemplateListResponseDTO> findByCriteriaWithGroups(
            String search, List<Long> templateGroupIds, Pageable pageable) {
        return ntRepository.findByCriteriaWithGroups(search, templateGroupIds, pageable);
    }
}
