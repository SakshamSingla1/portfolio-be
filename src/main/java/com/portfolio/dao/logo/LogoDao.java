package com.portfolio.dao.logo;

import com.portfolio.dtos.Logos.LogoDropdown;
import com.portfolio.dtos.Logos.LogoResponse;
import com.portfolio.entities.Logo;
import com.portfolio.repositories.LogoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class LogoDao {

    private final LogoRepository logoRepository;

    public LogoDao(LogoRepository logoRepository) {
        this.logoRepository = logoRepository;
    }

    public Logo save(Logo logo) {
        return logoRepository.save(logo);
    }

    public Optional<Logo> findById(Long id) {
        return logoRepository.findById(id);
    }

    public void deleteById(Long id) {
        logoRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return logoRepository.existsByName(name);
    }

    public Optional<LogoResponse> findDTOById(Long id) {
        return logoRepository.findDTOById(id);
    }

    public Page<LogoDropdown> findByCriteria(String search, Pageable pageable) {
        return logoRepository.findByCriteria(search, pageable);
    }

    public void delete(Logo logo) {
        logoRepository.delete(logo);
    }
}
