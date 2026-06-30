package com.portfolio.dao.profile;

import com.portfolio.dtos.DashboardDTOs.ActivityDTO;
import com.portfolio.dtos.DashboardDTOs.StatsDTO;
import com.portfolio.dtos.User.UserResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ProfileDao {

    private final ProfileRepository profileRepository;

    public ProfileDao(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    public Optional<Profile> findById(Long id) {
        return profileRepository.findById(id);
    }

    public void deleteById(Long id) {
        profileRepository.deleteById(id);
    }

    public Optional<Profile> findByEmail(String email) {
        return profileRepository.findByEmail(email);
    }

    public Optional<Profile> findByPhone(String phone) {
        return profileRepository.findByPhone(phone);
    }

    public Optional<Profile> findByUserName(String userName) {
        return profileRepository.findByUserName(userName);
    }

    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }

    public Page<UserResponse> findByCriteria(
            String search, List<StatusEnum> statuses, List<Long> roleIds, Pageable pageable) {
        return profileRepository.findByCriteria(search, statuses, roleIds, pageable);
    }

    public boolean existsById(Long id) {
        return profileRepository.existsById(id);
    }

    public StatsDTO getDashboardStats(Long profileId) {
        List<Object[]> rows = profileRepository.getDashboardStats(profileId);
        if (rows.isEmpty()) return StatsDTO.builder().build();
        Object[] r = rows.get(0);
        return StatsDTO.builder()
                .totalSkills(toLong(r[0]))
                .totalEducation(toLong(r[1]))
                .totalExperience(toLong(r[2]))
                .totalProjects(toLong(r[3]))
                .totalAchievements(toLong(r[4]))
                .totalTestimonials(toLong(r[5]))
                .totalCertification(toLong(r[6]))
                .totalMessages(toLong(r[7]))
                .unreadMessages(toLong(r[8]))
                .totalSocialLinks(toLong(r[9]))
                .build();
    }

    public List<ActivityDTO> getLatestActivities(Long profileId) {
        return profileRepository.getLatestActivities(profileId).stream()
                .map(row -> ActivityDTO.builder()
                        .type((String) row[0])
                        .description((String) row[1])
                        .timestamp(row[2] != null ? ((Timestamp) row[2]).toLocalDateTime() : null)
                        .entityId((String) row[3])
                        .build())
                .toList();
    }

    private long toLong(Object val) {
        return val == null ? 0L : ((Number) val).longValue();
    }
}
