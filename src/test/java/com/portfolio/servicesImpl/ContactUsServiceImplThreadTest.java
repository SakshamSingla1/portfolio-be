package com.portfolio.servicesImpl;

import com.portfolio.dao.contact_message.ContactMessageDao;
import com.portfolio.dao.contact_us.ContactUsDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.social_links.SocialLinksDao;
import com.portfolio.dtos.ContactUs.ContactMessageResponse;
import com.portfolio.dtos.ContactUs.ContactThreadResponse;
import com.portfolio.entities.ContactMessage;
import com.portfolio.entities.ContactUs;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.SenderTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.NTService;
import com.portfolio.services.NotificationService;
import com.portfolio.utils.Helper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Covers the two things that matter most about the new conversation-thread feature:
 * 1. getThreadForOwner must never return a thread for a contact message belonging to a
 *    different profile, even with a valid id -- otherwise any authenticated profile could read
 *    another profile's conversations just by guessing ids.
 * 2. buildThread's synthesized "first message" means every pre-existing ContactUs row (from
 *    before this feature existed) still renders as a valid thread with no backfill needed.
 */
class ContactUsServiceImplThreadTest {

    private final ContactUsDao contactUsDao = mock(ContactUsDao.class);
    private final ProfileDao profileDao = mock(ProfileDao.class);
    private final ContactMessageDao contactMessageDao = mock(ContactMessageDao.class);
    private final SocialLinksDao socialLinksDao = mock(SocialLinksDao.class);

    private final ContactUsServiceImpl service = new ContactUsServiceImpl(
            contactUsDao, profileDao, mock(NTService.class), mock(NotificationService.class),
            mock(Helper.class), contactMessageDao, socialLinksDao);

    private static final long OWNER_PROFILE_ID = 1L;
    private static final long OTHER_PROFILE_ID = 2L;
    private static final long CONTACT_ID = 100L;

    private ContactUs contact(long profileId) {
        ContactUs c = ContactUs.builder()
                .id(CONTACT_ID)
                .profileId(profileId)
                .name("Jane Visitor")
                .message("Hi, are you available for freelance work?")
                .build();
        return c;
    }

    @Test
    void getThreadForOwner_differentProfile_throwsNotFound() {
        when(contactUsDao.findById(CONTACT_ID)).thenReturn(Optional.of(contact(OTHER_PROFILE_ID)));

        assertThatThrownBy(() -> service.getThreadForOwner(CONTACT_ID, OWNER_PROFILE_ID))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.CONTACT_US_NOT_FOUND);
    }

    @Test
    void getThreadForOwner_ownProfile_startsWithSynthesizedOriginalMessage() throws Exception {
        when(contactUsDao.findById(CONTACT_ID)).thenReturn(Optional.of(contact(OWNER_PROFILE_ID)));
        when(contactMessageDao.findByContactUsId(CONTACT_ID)).thenReturn(List.of(
                ContactMessage.builder().id(1L).contactUsId(CONTACT_ID).senderType(SenderTypeEnum.OWNER).message("Yes, happy to chat!").build()
        ));
        when(profileDao.findById(OWNER_PROFILE_ID)).thenReturn(Optional.of(Profile.builder().id(OWNER_PROFILE_ID).fullName("John Owner").build()));

        ContactThreadResponse thread = service.getThreadForOwner(CONTACT_ID, OWNER_PROFILE_ID);

        assertThat(thread.getMessages()).hasSize(2);
        assertThat(thread.getMessages().get(0).getSenderType()).isEqualTo("VISITOR");
        assertThat(thread.getMessages().get(0).getMessage()).isEqualTo("Hi, are you available for freelance work?");
        assertThat(thread.getMessages().get(1).getSenderType()).isEqualTo("OWNER");
        assertThat(thread.getProfileName()).isEqualTo("John Owner");
    }

    @Test
    void getThreadByToken_unknownToken_throwsNotFound() {
        when(contactUsDao.findByReplyToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getThreadByToken("bad-token"))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.CONTACT_US_NOT_FOUND);
    }

    @Test
    void addVisitorReply_savesMessageAsVisitorAndNotifiesOwner() throws Exception {
        ContactUs contact = contact(OWNER_PROFILE_ID);
        when(contactUsDao.findByReplyToken("good-token")).thenReturn(Optional.of(contact));
        when(contactMessageDao.save(any(ContactMessage.class))).thenAnswer(inv -> {
            ContactMessage m = inv.getArgument(0);
            m.setId(5L);
            return m;
        });
        when(profileDao.findById(OWNER_PROFILE_ID)).thenReturn(Optional.of(
                Profile.builder().id(OWNER_PROFILE_ID).fullName("John Owner").email("john@example.com").build()));

        ContactMessageResponse response = service.addVisitorReply("good-token", "Just following up!");

        assertThat(response.getSenderType()).isEqualTo("VISITOR");
        assertThat(response.getMessage()).isEqualTo("Just following up!");
        verify(contactMessageDao).save(argThat(m ->
                m.getSenderType() == SenderTypeEnum.VISITOR
                        && m.getContactUsId().equals(CONTACT_ID)
                        && "Just following up!".equals(m.getMessage())
        ));
    }
}
