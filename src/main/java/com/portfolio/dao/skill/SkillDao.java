package com.portfolio.dao.skill;

import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.entities.Skill;
import com.portfolio.enums.SkillLevelEnum;
import com.portfolio.repositories.SkillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class SkillDao {

    private final SkillRepository skillRepository;

    public SkillDao(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill save(Skill skill) {
        return skillRepository.save(skill);
    }

    public Optional<Skill> findById(Long id) {
        return skillRepository.findById(id);
    }

    public void deleteById(Long id) {
        skillRepository.deleteById(id);
    }

    public Optional<SkillResponse> findDTOById(Long id) {
        return skillRepository.findDTOById(id);
    }

    public Page<SkillResponse> findByCriteria(Long profileId, String search, Pageable pageable) {
        return skillRepository.findByCriteria(profileId, search, pageable);
    }

    public List<Skill> findByProfileId(Long profileId) {
        return skillRepository.findByProfileId(profileId);
    }

    public boolean existsByLogoIdAndProfileId(Long logoId, Long profileId) {
        return skillRepository.existsByLogoIdAndProfileId(logoId, profileId);
    }

    public long countByProfileId(Long profileId) {
        return skillRepository.countByProfileId(profileId);
    }

    public Optional<Skill> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId) {
        return skillRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);
    }

    public Long countByLevel(SkillLevelEnum level) {
        return skillRepository.countByLevel(level);
    }

    public void delete(Skill skill) {
        skillRepository.delete(skill);
    }

    public List<Skill> findAllById(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }

    public List<SkillDropdown> findDropdownByIds(List<Long> ids) {
        return skillRepository.findDropdownByIds(ids);
    }
}
