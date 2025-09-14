package com.portfolio.services;

import com.portfolio.dtos.ContactUsRequest;
import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.entities.ContactUs;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ContactUsRepository;
import com.portfolio.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for managing ContactUs entities.
 * Handles creation, email notifications, and paginated retrieval by profile.
 */
@Service
public class ContactUsService {

    @Autowired
    private ContactUsRepository contactUsRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MailService mailService;

    // 🔹 CREATE CONTACT AND SEND EMAIL
    public ContactUsResponse create(ContactUsRequest request) throws GenericException {
        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));

        ContactUs contact = ContactUs.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .message(request.getMessage())
                .profile(profile)
                .build();

        ContactUs saved = contactUsRepository.save(contact);

        if (profile.getEmail() != null) {
            mailService.sendContactUsEmail(
                    profile.getEmail(),
                    request.getName(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getMessage()
            );
        }

        return toDto(saved);
    }

    // 🔹 PAGINATED SEARCH BY PROFILE
    public Page<ContactUsResponse> getContactUsByProfileId(Integer profileId, Pageable pageable, String search) throws GenericException {
        Page<ContactUs> page = contactUsRepository.findByProfileIdWithSearch(
                profileId, (search != null ? search.trim() : null), pageable
        );
        return page.map(this::toDto);
    }

    // 🔹 ENTITY → DTO
    private ContactUsResponse toDto(ContactUs contact) {
        return ContactUsResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .message(contact.getMessage())
                .created(contact.getCreated())
                .build();
    }
}
