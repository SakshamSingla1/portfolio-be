package com.portfolio.dao.profile_completion;

import com.portfolio.entities.ProfileCompletionSnapshot;
import com.portfolio.repositories.ProfileCompletionSnapshotRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ProfileCompletionSnapshotDao {

    private final ProfileCompletionSnapshotRepository repository;

    public ProfileCompletionSnapshotDao(ProfileCompletionSnapshotRepository repository) {
        this.repository = repository;
    }

    public void upsertSnapshot(Long profileId, LocalDate snapshotDate, Integer percentage) {
        repository.upsertSnapshot(profileId, snapshotDate, percentage);
    }

    public List<ProfileCompletionSnapshot> findSince(Long profileId, LocalDate since) {
        return repository.findByProfileIdAndSnapshotDateGreaterThanEqualOrderBySnapshotDateAsc(profileId, since);
    }
}
