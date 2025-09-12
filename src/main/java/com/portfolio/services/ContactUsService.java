package com.portfolio.services;

import com.portfolio.dtos.ContactUsRequest;
import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.entities.ContactUs;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ContactUsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactUsService {

    @Autowired
    private ContactUsRepository contactUsRepository;

    public ResponseEntity<ResponseModel<ContactUsResponse>> create(ContactUsRequest contactUs) {
        ContactUs contact = ContactUs.builder()
                .name(contactUs.getName())
                .email(contactUs.getEmail())
                .phone(contactUs.getPhone())
                .message(contactUs.getMessage())
                .build();
        ContactUs saved = contactUsRepository.save(contact);
        ContactUsResponse response = toDto(saved);
        return ApiResponse.successResponse(response, "Contact Us message sent successfully");
    }

    public ResponseEntity<ResponseModel<ContactUsResponse>> getById(Integer id) {
        Optional<ContactUs> optionalContact = contactUsRepository.findById(id);
        if (optionalContact.isEmpty()) {
            return ApiResponse.failureResponse(null, "Contact Us data not found");
        }
        return ApiResponse.successResponse(toDto(optionalContact.get()), "Contact Us data fetched successfully");
    }

    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> getByName(String name) {
        List<ContactUs> contacts = contactUsRepository.findByName(name);
        if (contacts == null || contacts.isEmpty()) {
            return ApiResponse.failureResponse(null, "No Contact Us data found with the given name");
        }
        return ApiResponse.successResponse(toDtoList(contacts), "Contact Us data fetched successfully");
    }

    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> getByEmail(String email) {
        List<ContactUs> contacts = contactUsRepository.findByEmail(email);
        if (contacts == null || contacts.isEmpty()) {
            return ApiResponse.failureResponse(null, "No Contact Us data found with the given email");
        }
        return ApiResponse.successResponse(toDtoList(contacts), "Contact Us data fetched successfully");
    }

    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> getByPhone(String phone) {
        List<ContactUs> contacts = contactUsRepository.findByPhone(phone);
        if (contacts == null || contacts.isEmpty()) {
            return ApiResponse.failureResponse(null, "No Contact Us data found with the given phone number");
        }
        return ApiResponse.successResponse(toDtoList(contacts), "Contact Us data fetched successfully");
    }

    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> getAllContactUs() {
        List<ContactUs> contacts = contactUsRepository.findAll();
        if (contacts == null || contacts.isEmpty()) {
            return ApiResponse.failureResponse(null, "No Contact Us data available");
        }
        return ApiResponse.successResponse(toDtoList(contacts), "All Contact Us data fetched successfully");
    }

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

    private List<ContactUsResponse> toDtoList(List<ContactUs> contacts) {
        return contacts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
