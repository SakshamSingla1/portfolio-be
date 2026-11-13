package com.portfolio.dao.guestbook;

import com.portfolio.entities.GuestbookEntry;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.GuestbookEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class GuestbookEntryDao {

    private final GuestbookEntryRepository guestbookEntryRepository;

    public GuestbookEntryDao(GuestbookEntryRepository guestbookEntryRepository) {
        this.guestbookEntryRepository = guestbookEntryRepository;
    }

    public GuestbookEntry save(GuestbookEntry entry) {
        return guestbookEntryRepository.save(entry);
    }

    public Optional<GuestbookEntry> findById(Long id) {
        return guestbookEntryRepository.findById(id);
    }

    public Page<GuestbookEntry> findActiveByProfileId(Long profileId, Pageable pageable) {
        return guestbookEntryRepository.findByProfileIdAndStatusOrderByCreatedAtDesc(profileId, StatusEnum.ACTIVE, pageable);
    }

    public Page<GuestbookEntry> findAllByProfileId(Long profileId, Pageable pageable) {
        return guestbookEntryRepository.findByProfileIdOrderByCreatedAtDesc(profileId, pageable);
    }

    public void deleteById(Long id) {
        guestbookEntryRepository.deleteById(id);
    }
}
