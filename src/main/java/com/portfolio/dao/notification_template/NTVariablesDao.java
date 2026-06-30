package com.portfolio.dao.notification_template;

import com.portfolio.dtos.NotificationTemplates.NotificationTemplateVariablesListResponseDTO;
import com.portfolio.entities.NotificationTemplateVariables;
import com.portfolio.repositories.NTVariablesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class NTVariablesDao {

    private final NTVariablesRepository ntVariablesRepository;

    public NTVariablesDao(NTVariablesRepository ntVariablesRepository) {
        this.ntVariablesRepository = ntVariablesRepository;
    }

    public NotificationTemplateVariables save(NotificationTemplateVariables variables) {
        return ntVariablesRepository.save(variables);
    }

    public Optional<NotificationTemplateVariables> findById(Long id) {
        return ntVariablesRepository.findById(id);
    }

    public void deleteById(Long id) {
        ntVariablesRepository.deleteById(id);
    }

    public Page<NotificationTemplateVariablesListResponseDTO> findByCriteria(String search, Pageable pageable) {
        return ntVariablesRepository.findByCriteria(search, pageable);
    }
}
