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

    SkillResponse update(String id, SkillRequest request) throws GenericException;

    SkillResponse getById(String id) throws GenericException;

    void delete(String id) throws GenericException;

    Page<SkillResponse> getByProfile(String profileId, Pageable pageable, String search, String sortDir, String sortBy);

    List<SkillResponse> getByProfile(String profileId);

    SkillStat getStats();
}
