package com.portfolio.services;

import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.entities.Logo;
import com.portfolio.repositories.LogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogoService {

    @Autowired
    private LogoRepository logoRepository;

    /**
     * Returns a paginated list of logos with optional search filter
     */
    public Page<LogoDropdown> getAllLogosByPage(Pageable pageable, String search) {
        return logoRepository.findAllWithPagination(search, pageable);
    }
}
