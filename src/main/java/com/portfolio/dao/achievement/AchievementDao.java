package com.portfolio.dao.achievement;

import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.entities.Achievements;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.AchievementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class AchievementDao {

    private final AchievementRepository achievementRepository;

    public AchievementDao(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public Achievements save(Achievements achievement) {
        return achievementRepository.save(achievement);
    }

    public Optional<Achievements> findById(Long id) {
        return achievementRepository.findById(id);
    }

    public void deleteById(Long id) {
        achievementRepository.deleteById(id);
    }

    public boolean existsByProfileIdAndOrder(Long profileId, String order) {
        return achievementRepository.existsByProfileIdAndOrder(profileId, order);
    }

    public boolean existsByProfileIdAndOrderAndIdNot(Long profileId, String order, Long id) {
        return achievementRepository.existsByProfileIdAndOrderAndIdNot(profileId, order, id);
    }

    public List<Achievements> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum status) {
        return achievementRepository.findByProfileIdAndStatusOrderByOrderAsc(profileId, status);
    }

    public long countByProfileId(Long profileId) {
        return achievementRepository.countByProfileId(profileId);
    }

    public Optional<Achievements> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId) {
        return achievementRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);
    }

    public boolean existsById(Long id) {
        return achievementRepository.existsById(id);
    }

    public Optional<AchievementResponseDTO> findDTOById(Long id) {
        return achievementRepository.findDTOById(id);
    }

    public Page<AchievementResponseDTO> findByCriteria(Long profileId, String search, Pageable pageable){
        return achievementRepository.findByCriteria(profileId,search,pageable);
    }
}
