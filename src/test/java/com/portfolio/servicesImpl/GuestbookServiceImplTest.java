package com.portfolio.servicesImpl;

import com.portfolio.dao.guestbook.GuestbookEntryDao;
import com.portfolio.dtos.Guestbook.GuestbookEntryRequest;
import com.portfolio.dtos.Guestbook.GuestbookEntryResponse;
import com.portfolio.entities.GuestbookEntry;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * The one rule that matters most here: setVisibility/deleteEntry must never act on an entry that
 * belongs to a different profile, even when given a valid entry id -- otherwise any authenticated
 * profile could hide or delete another profile's guestbook entries just by guessing ids.
 */
class GuestbookServiceImplTest {

    private final GuestbookEntryDao guestbookEntryDao = mock(GuestbookEntryDao.class);
    private final GuestbookServiceImpl service = new GuestbookServiceImpl(guestbookEntryDao);

    private static final long OWNER_PROFILE_ID = 1L;
    private static final long OTHER_PROFILE_ID = 2L;
    private static final long ENTRY_ID = 100L;

    @Test
    void addEntry_savesTrimmedFieldsAsActive() {
        when(guestbookEntryDao.save(any(GuestbookEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        GuestbookEntryRequest req = new GuestbookEntryRequest();
        req.setVisitorName("  Jane Doe  ");
        req.setMessage("  Great portfolio!  ");

        GuestbookEntryResponse response = service.addEntry(OWNER_PROFILE_ID, req);

        assertThat(response.getVisitorName()).isEqualTo("Jane Doe");
        assertThat(response.getMessage()).isEqualTo("Great portfolio!");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        verify(guestbookEntryDao).save(argThat(e -> e.getStatus() == StatusEnum.ACTIVE && e.getProfileId().equals(OWNER_PROFILE_ID)));
    }

    @Test
    void setVisibility_entryBelongsToDifferentProfile_throwsNotFound() {
        GuestbookEntry entry = GuestbookEntry.builder().id(ENTRY_ID).profileId(OTHER_PROFILE_ID).status(StatusEnum.ACTIVE).build();
        when(guestbookEntryDao.findById(ENTRY_ID)).thenReturn(Optional.of(entry));

        assertThatThrownBy(() -> service.setVisibility(OWNER_PROFILE_ID, ENTRY_ID, false))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.GUESTBOOK_ENTRY_NOT_FOUND);

        verify(guestbookEntryDao, never()).save(any());
    }

    @Test
    void deleteEntry_entryBelongsToDifferentProfile_throwsNotFound_andNeverDeletes() {
        GuestbookEntry entry = GuestbookEntry.builder().id(ENTRY_ID).profileId(OTHER_PROFILE_ID).status(StatusEnum.ACTIVE).build();
        when(guestbookEntryDao.findById(ENTRY_ID)).thenReturn(Optional.of(entry));

        assertThatThrownBy(() -> service.deleteEntry(OWNER_PROFILE_ID, ENTRY_ID))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.GUESTBOOK_ENTRY_NOT_FOUND);

        verify(guestbookEntryDao, never()).deleteById(any());
    }

    @Test
    void setVisibility_ownEntry_updatesStatus() throws Exception {
        GuestbookEntry entry = GuestbookEntry.builder().id(ENTRY_ID).profileId(OWNER_PROFILE_ID).status(StatusEnum.ACTIVE).build();
        when(guestbookEntryDao.findById(ENTRY_ID)).thenReturn(Optional.of(entry));

        service.setVisibility(OWNER_PROFILE_ID, ENTRY_ID, false);

        verify(guestbookEntryDao).save(argThat(e -> e.getStatus() == StatusEnum.INACTIVE));
    }

    @Test
    void deleteEntry_ownEntry_deletesById() throws Exception {
        GuestbookEntry entry = GuestbookEntry.builder().id(ENTRY_ID).profileId(OWNER_PROFILE_ID).status(StatusEnum.ACTIVE).build();
        when(guestbookEntryDao.findById(ENTRY_ID)).thenReturn(Optional.of(entry));

        service.deleteEntry(OWNER_PROFILE_ID, ENTRY_ID);

        verify(guestbookEntryDao).deleteById(ENTRY_ID);
    }
}
