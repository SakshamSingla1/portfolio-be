package com.portfolio.servicesImpl;

import com.portfolio.dtos.ContactUsRequest;
import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.entities.ContactUs;
import com.portfolio.entities.NotificationTemplate;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ContactUsRepository;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.services.NTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ContactUsService {

    private final ContactUsRepository contactUsRepository;
    private final ProfileRepository profileRepository;
    private final NTService ntService;

    public ContactUsResponse create(ContactUsRequest request) throws GenericException {
        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.INVALID_ARGUMENT, "Profile not found"));
        ContactUs contact = ContactUs.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .message(request.getMessage())
                .profileId(request.getProfileId())
                .createdAt(LocalDateTime.now())
                .build();
        ContactUs saved = contactUsRepository.save(contact);
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
                e.getMessage();
            }
        }
        return toDto(saved);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public Page<ContactUsResponse> getContactUsByProfileId(String profileId, Pageable pageable, String search,String sortBy, String sortDir) throws GenericException {
        Sort sort = Sort.by("desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt");
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasProfileId = profileId != null;
        Page<ContactUs> contactUses;
        if( hasSearch && hasProfileId){
            contactUses = contactUsRepository.findByProfileIdWithSearch(profileId,search,sortedPageable);
        }else if(hasSearch){
            contactUses = contactUsRepository.findBySearch(search,sortedPageable);
        }else if(hasProfileId) {
            contactUses = contactUsRepository.findByProfileId(profileId, sortedPageable);
        }else{
            contactUses = contactUsRepository.findAll(sortedPageable);
        }
        return contactUses.map(this::toDto);
    }

    private ContactUsResponse toDto(ContactUs contact) {
        return ContactUsResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .message(contact.getMessage())
                .createdAt(contact.getCreatedAt())
                .build();
    }
}
