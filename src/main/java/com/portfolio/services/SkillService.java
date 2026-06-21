package com.portfolio.services;

import com.portfolio.dtos.Skill.SkillRequest;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.dtos.Skill.SkillStat;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkillService {

    SkillResponse create(SkillRequest request) throws GenericException;

    SkillResponse update(Long id, SkillRequest request) throws GenericException;

    SkillResponse getById(Long id) throws GenericException;

    void delete(Long id) throws GenericException;

    Page<SkillResponse> getByProfile(Long profileId, Pageable pageable, String search, String sortDir, String sortBy);

    List<SkillResponse> getByProfile(Long profileId);

    SkillStat getStats();
}
