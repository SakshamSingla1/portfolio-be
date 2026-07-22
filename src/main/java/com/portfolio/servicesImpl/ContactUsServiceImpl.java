package com.portfolio.servicesImpl;

import com.portfolio.dao.contact_us.ContactUsDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.entities.ContactUs;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ContactUsStatusEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.NotificationTypeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ContactUsService;
import com.portfolio.services.EmailService;
import com.portfolio.services.NTService;
import com.portfolio.services.NotificationService;
import com.portfolio.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContactUsServiceImpl implements ContactUsService{

    private final ContactUsDao contactUsDao;
    private final ProfileDao profileDao;
    private final NTService ntService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final Helper helper;

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
    public ContactUsResponse reply(Long id, String replyMessage, String authHeader) throws GenericException {
        Profile profile = helper.getProfileFromHeader(authHeader);
        ContactUs contact = contactUsDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.CONTACT_US_NOT_FOUND, "Message not found"));
        if (!contact.getProfileId().equals(profile.getId())) {
            throw new GenericException(ExceptionCodeEnum.UNAUTHORIZED, "Not authorized to reply to this message");
        }
        String subject = "Re: Your message to " + safe(profile.getFullName());
        String html = buildReplyHtml(safe(profile.getFullName()), safe(contact.getName()), safe(contact.getMessage()), safe(replyMessage));
        emailService.sendEmail(contact.getEmail(), subject, html);
        contact.setReplyMessage(replyMessage);
        contact.setRepliedAt(LocalDateTime.now());
        contact.setStatus(ContactUsStatusEnum.REPLIED);
        return toDto(contactUsDao.save(contact));
    }

    private String buildReplyHtml(String profileName, String contactName, String originalMessage, String replyMessage) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;max-width:600px;margin:0 auto;padding:32px 24px;background:#f9fafb;">
                  <div style="background:#ffffff;border-radius:12px;padding:32px;border:1px solid #e5e7eb;">
                    <h2 style="margin:0 0 8px;color:#111827;font-size:20px;">Message from %s</h2>
                    <p style="margin:0 0 24px;color:#6b7280;font-size:14px;">Hi %s, %s replied to your message.</p>
                    <div style="background:#f3f4f6;border-radius:8px;padding:20px;margin-bottom:24px;">
                      <p style="margin:0;color:#111827;font-size:15px;line-height:1.6;white-space:pre-wrap;">%s</p>
                    </div>
                    <hr style="border:none;border-top:1px solid #e5e7eb;margin:24px 0;" />
                    <p style="margin:0 0 8px;color:#9ca3af;font-size:12px;text-transform:uppercase;letter-spacing:.05em;">Your original message</p>
                    <p style="margin:0;color:#6b7280;font-size:13px;line-height:1.6;white-space:pre-wrap;">%s</p>
                  </div>
                </body>
                </html>
                """.formatted(profileName, contactName, profileName, replyMessage, originalMessage);
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
