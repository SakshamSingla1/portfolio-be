package com.portfolio.services;

import com.portfolio.dtos.Guestbook.GuestbookEntryRequest;
import com.portfolio.dtos.Guestbook.GuestbookEntryResponse;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuestbookService {

    /** ACTIVE entries only -- the public portfolio page's guestbook list. */
    Page<GuestbookEntryResponse> getPublicEntries(Long profileId, Pageable pageable);

    GuestbookEntryResponse addEntry(Long profileId, GuestbookEntryRequest req);

    /** Every entry (ACTIVE and INACTIVE) for the authenticated owner's own moderation view. */
    Page<GuestbookEntryResponse> getForOwner(Long profileId, Pageable pageable);

    void setVisibility(Long profileId, Long entryId, boolean visible) throws GenericException;

    void deleteEntry(Long profileId, Long entryId) throws GenericException;
}
