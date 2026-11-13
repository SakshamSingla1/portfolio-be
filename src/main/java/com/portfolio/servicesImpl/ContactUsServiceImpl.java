package com.portfolio.servicesImpl;

import com.portfolio.dao.contact_message.ContactMessageDao;
import com.portfolio.dao.contact_us.ContactUsDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.social_links.SocialLinksDao;
import com.portfolio.dtos.ContactUs.ContactMessageResponse;
import com.portfolio.dtos.ContactUs.ContactThreadResponse;
import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.entities.ContactMessage;
import com.portfolio.entities.ContactUs;
import com.portfolio.entities.Profile;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.ContactUsStatusEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.NotificationTypeEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.SenderTypeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ContactUsService;
import com.portfolio.services.NTService;
import com.portfolio.services.NotificationService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactUsServiceImpl implements ContactUsService{

    private final ContactUsDao contactUsDao;
    private final ProfileDao profileDao;
    private final NTService ntService;
    private final NotificationService notificationService;
    private final Helper helper;
    private final ContactMessageDao contactMessageDao;
    private final SocialLinksDao socialLinksDao;

    @Value("${portfolio.public.base-url:http://localhost:5173}")
    private String defaultPublicBaseUrl;

    @Override
    public ContactUsResponse create(ContactUsRequest request) throws GenericException {
        Profile profile = profileDao.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));
        ContactUs contact = ContactUs.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .message(request.getMessage())
                .status(ContactUsStatusEnum.UNREAD)
                .profileId(request.getProfileId())
                .build();
        ContactUs saved = contactUsDao.save(contact);
        if (profile.getEmail() != null) {
            try {
                ntService.sendNotification(
                        "CONTACT-US",
                        Map.of(
                                "profileName", safe(profile.getFullName()),
                                "senderName", safe(request.getName()),
                                "senderEmail", safe(request.getEmail()),
                                "senderPhone", safe(request.getPhone()),
                                "message", safe(request.getMessage())
                        ),
                        profile.getEmail()
                );
            } catch (Exception e) {
                // notification failure is non-fatal; contact-us record is already saved
            }
        }
        notificationService.create(
                request.getProfileId(),
                NotificationTypeEnum.CONTACT_MESSAGE,
                "New contact message",
                safe(request.getName()) + " sent you a message",
                "/messages"
        );
        return toDto(saved);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public Page<ContactUsResponse> getContactUsByProfileId(Long profileId, String search, ContactUsStatusEnum status,Pageable pageable) throws GenericException {
        return contactUsDao.findByCriteria(profileId,search,status,pageable);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ContactUsStatusEnum status)
            throws GenericException {

        if (!contactUsDao.existsById(id)) {
            throw new GenericException(
                    ExceptionCodeEnum.CONTACT_US_NOT_FOUND,
                    "Message not found"
            );
        }

        contactUsDao.updateStatusById(id, status);
    }

    @Override
    @Transactional
    public int updateStatusBulk(List<Long> ids, ContactUsStatusEnum status) throws GenericException {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return contactUsDao.updateStatusByIdIn(ids, status);
    }

    @Override
    @Transactional
    public ContactUsResponse reply(Long id, String replyMessage, String authHeader) throws GenericException {
        Profile profile = helper.getProfileFromHeader(authHeader);
        ContactUs contact = contactUsDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CONTACT_US_NOT_FOUND, "Message not found"));
        if (!contact.getProfileId().equals(profile.getId())) {
            throw new GenericException(ExceptionCodeEnum.UNAUTHORIZED, "Not authorized to reply to this message");
        }
        if (contact.getReplyToken() == null) {
            contact.setReplyToken(UUID.randomUUID().toString().replace("-", ""));
        }
        ntService.sendNotification(
                "CONTACT-REPLY",
                Map.of(
                        "profileName", safe(profile.getFullName()),
                        "contactName", safe(contact.getName()),
                        "replyMessage", safe(replyMessage),
                        "originalMessage", safe(contact.getMessage()),
                        "threadUrl", resolvePublicBaseUrl(contact.getProfileId()) + "/contact-thread/" + contact.getReplyToken()
                ),
                contact.getEmail()
        );
        contactMessageDao.save(ContactMessage.builder()
                .contactUsId(contact.getId())
                .senderType(SenderTypeEnum.OWNER)
                .message(replyMessage)
                .build());
        contact.setReplyMessage(replyMessage);
        contact.setRepliedAt(LocalDateTime.now());
        contact.setStatus(ContactUsStatusEnum.REPLIED);
        return toDto(contactUsDao.save(contact));
    }

    @Override
    public ContactThreadResponse getThreadForOwner(Long contactUsId, Long profileId) throws GenericException {
        ContactUs contact = contactUsDao.findById(contactUsId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CONTACT_US_NOT_FOUND, "Message not found"));
        if (!contact.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.CONTACT_US_NOT_FOUND, "Message not found");
        }
        return buildThread(contact);
    }

    @Override
    public ContactThreadResponse getThreadByToken(String token) throws GenericException {
        ContactUs contact = contactUsDao.findByReplyToken(token)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CONTACT_US_NOT_FOUND, "Conversation not found"));
        return buildThread(contact);
    }

    @Override
    @Transactional
    public ContactMessageResponse addVisitorReply(String token, String message) throws GenericException {
        ContactUs contact = contactUsDao.findByReplyToken(token)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CONTACT_US_NOT_FOUND, "Conversation not found"));
        ContactMessage saved = contactMessageDao.save(ContactMessage.builder()
                .contactUsId(contact.getId())
                .senderType(SenderTypeEnum.VISITOR)
                .message(message)
                .build());

        profileDao.findById(contact.getProfileId()).ifPresent(profile -> {
            if (profile.getEmail() != null) {
                try {
                    ntService.sendNotification(
                            "CONTACT-THREAD-REPLY",
                            Map.of(
                                    "profileName", safe(profile.getFullName()),
                                    "contactName", safe(contact.getName()),
                                    "message", safe(message)
                            ),
                            profile.getEmail()
                    );
                } catch (Exception e) {
                    // notification failure is non-fatal; the reply is already saved
                }
            }
        });
        notificationService.create(
                contact.getProfileId(),
                NotificationTypeEnum.CONTACT_MESSAGE,
                "New reply",
                safe(contact.getName()) + " replied to their message",
                "/messages"
        );

        return mapMessage(saved);
    }

    // The conversation's first message is ContactUs's own name/message fields, not a
    // ContactMessage row -- synthesizing it here at read time means the existing single-reply
    // data (every ContactUs row from before this feature existed) renders as a valid one- or
    // two-message thread with no backfill migration needed.
    private ContactThreadResponse buildThread(ContactUs contact) {
        List<ContactMessageResponse> messages = new ArrayList<>();
        messages.add(ContactMessageResponse.builder()
                .id(contact.getId())
                .senderType(SenderTypeEnum.VISITOR.name())
                .message(contact.getMessage())
                .createdAt(contact.getCreatedAt())
                .build());
        contactMessageDao.findByContactUsId(contact.getId()).forEach(m -> messages.add(mapMessage(m)));

        Profile profile = profileDao.findById(contact.getProfileId()).orElse(null);
        return ContactThreadResponse.builder()
                .contactUsId(contact.getId())
                .visitorName(contact.getName())
                .profileName(profile != null ? profile.getFullName() : null)
                .messages(messages)
                .build();
    }

    private ContactMessageResponse mapMessage(ContactMessage message) {
        return ContactMessageResponse.builder()
                .id(message.getId())
                .senderType(message.getSenderType().name())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .build();
    }

    // Mirrors TestimonialLinkServiceImpl#resolvePublicBaseUrl -- prefers the profile's own
    // custom domain (if they've set one as their PORTFOLIO social link) so the emailed link is
    // branded, falling back to the shared default otherwise.
    private String resolvePublicBaseUrl(Long profileId) {
        return socialLinksDao.findByProfileIdAndPlatformAndStatus(profileId, PlatformEnum.PORTFOLIO, StatusEnum.ACTIVE)
                .map(SocialLinks::getUrl)
                .map(url -> url.endsWith("/") ? url.substring(0, url.length() - 1) : url)
                .orElse(defaultPublicBaseUrl);
    }

    @Override
    @Transactional
    public void deleteContactUs(Long id, Long profileId) throws GenericException {
        ContactUs contact = contactUsDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CONTACT_US_NOT_FOUND, "Message not found"));
        if (!contact.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.FORBIDDEN, "You do not have permission to delete this message");
        }
        contactUsDao.deleteById(id);
    }

    private ContactUsResponse toDto(ContactUs contact) {
        return ContactUsResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .message(contact.getMessage())
                .status(contact.getStatus())
                .createdAt(contact.getCreatedAt())
                .replyMessage(contact.getReplyMessage())
                .repliedAt(contact.getRepliedAt())
                .build();
    }
}
