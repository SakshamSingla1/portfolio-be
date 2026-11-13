package com.portfolio.servicesImpl;

import com.portfolio.dao.guestbook.GuestbookEntryDao;
import com.portfolio.dtos.Guestbook.GuestbookEntryRequest;
import com.portfolio.dtos.Guestbook.GuestbookEntryResponse;
import com.portfolio.entities.GuestbookEntry;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.GuestbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService {

    private final GuestbookEntryDao guestbookEntryDao;

    @Override
    public Page<GuestbookEntryResponse> getPublicEntries(Long profileId, Pageable pageable) {
        return guestbookEntryDao.findActiveByProfileId(profileId, pageable).map(this::mapToResponse);
    }

    @Override
    public GuestbookEntryResponse addEntry(Long profileId, GuestbookEntryRequest req) {
        GuestbookEntry entry = GuestbookEntry.builder()
                .profileId(profileId)
                .visitorName(req.getVisitorName().trim())
                .message(req.getMessage().trim())
                .status(StatusEnum.ACTIVE)
                .build();
        return mapToResponse(guestbookEntryDao.save(entry));
    }

    @Override
    public Page<GuestbookEntryResponse> getForOwner(Long profileId, Pageable pageable) {
        return guestbookEntryDao.findAllByProfileId(profileId, pageable).map(this::mapToResponse);
    }

    @Override
    public void setVisibility(Long profileId, Long entryId, boolean visible) throws GenericException {
        GuestbookEntry entry = requireOwnedEntry(profileId, entryId);
        entry.setStatus(visible ? StatusEnum.ACTIVE : StatusEnum.INACTIVE);
        guestbookEntryDao.save(entry);
    }

    @Override
    public void deleteEntry(Long profileId, Long entryId) throws GenericException {
        requireOwnedEntry(profileId, entryId);
        guestbookEntryDao.deleteById(entryId);
    }

    // Scopes every owner-management mutation to the caller's own profileId -- without this, any
    // authenticated profile could hide/delete another profile's guestbook entries just by
    // guessing ids. Returns the not-found code rather than forbidden, so a mismatch doesn't
    // confirm the entry exists at all.
    private GuestbookEntry requireOwnedEntry(Long profileId, Long entryId) throws GenericException {
        GuestbookEntry entry = guestbookEntryDao.findById(entryId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.GUESTBOOK_ENTRY_NOT_FOUND, "Guestbook entry not found"));
        if (!entry.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.GUESTBOOK_ENTRY_NOT_FOUND, "Guestbook entry not found");
        }
        return entry;
    }

    private GuestbookEntryResponse mapToResponse(GuestbookEntry entry) {
        return GuestbookEntryResponse.builder()
                .id(entry.getId())
                .visitorName(entry.getVisitorName())
                .message(entry.getMessage())
                .createdAt(entry.getCreatedAt())
                .status(entry.getStatus() != null ? entry.getStatus().name() : null)
                .build();
    }
}
