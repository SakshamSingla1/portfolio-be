package com.portfolio.repositories;

import com.portfolio.entities.ProfileCompletionSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProfileCompletionSnapshotRepository extends JpaRepository<ProfileCompletionSnapshot, Long> {

    List<ProfileCompletionSnapshot> findByProfileIdAndSnapshotDateGreaterThanEqualOrderBySnapshotDateAsc(
            Long profileId, LocalDate since);

    // Upsert-on-conflict: at most one snapshot per profile per day, so re-visiting
    // the dashboard several times in a day just overwrites today's row instead of
    // piling up duplicates the trend chart would have to dedupe.
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO profile_completion_snapshots (profile_id, snapshot_date, percentage)
            VALUES (:profileId, :snapshotDate, :percentage)
            ON CONFLICT (profile_id, snapshot_date)
            DO UPDATE SET percentage = :percentage, updated_at = CURRENT_TIMESTAMP
            """, nativeQuery = true)
    void upsertSnapshot(@Param("profileId") Long profileId, @Param("snapshotDate") LocalDate snapshotDate, @Param("percentage") Integer percentage);
}
