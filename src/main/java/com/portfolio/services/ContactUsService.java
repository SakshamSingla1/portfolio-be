package com.portfolio.services;

import com.portfolio.dtos.ContactUsRequest;
import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.entities.ContactUs;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.ContactUsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactUsService {

    @Autowired
    ContactUsRepository contactUsRepository;

    public ResponseEntity<ResponseModel<ContactUsResponse>> create(ContactUsRequest contactUs) {
        ContactUs contact = ContactUs.builder()
                .name(contactUs.getName())
                .email(contactUs.getEmail())
                .phone(contactUs.getPhone())
                .message(contactUs.getMessage())
                .build();
        ContactUs saved = contactUsRepository.save(contact);
        ContactUsResponse response = ContactUsResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .message(saved.getMessage())
                .created(saved.getCreated())
                .build();
        return ApiResponse.successResponse(response,"Contact Us send successfully");
    }

    public ResponseEntity<ResponseModel<ContactUsResponse>> getById(Integer id) throws GenericException {
        ContactUs contact = contactUsRepository.findById(id).get();
        if(contact == null){
            return ApiResponse.failureResponse(null,"Contact Us data not present");
        }
        ContactUsResponse response = ContactUsResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .message(contact.getMessage())
                .phone(contact.getPhone())
                .created(contact.getCreated())
                .build();
        return ApiResponse.successResponse(response,"Contact Us fetched successfully");
    }

    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> getByName(String name) throws GenericException {
        List<ContactUs> contacts = contactUsRepository.findByName(name);
        if(contacts == null){
            return ApiResponse.failureResponse(null,"Contact Us data not present");
        }
        List<ContactUsResponse> response = contacts.stream()
                .map(contact -> ContactUsResponse.builder()
                        .id(contact.getId())
                        .name(contact.getName())
                        .email(contact.getEmail())
                        .message(contact.getMessage())
                        .phone(contact.getPhone())
                        .created(contact.getCreated())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.successResponse(response,"Contact Us fetched successfully");
    }

    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> getByEmail(String email) throws GenericException {
        List<ContactUs> contacts = contactUsRepository.findByEmail(email);
        if(contacts == null){
            return ApiResponse.failureResponse(null,"Contact Us data not present");
        }
        List<ContactUsResponse> response = contacts.stream()
                .map(contact -> ContactUsResponse.builder()
                        .id(contact.getId())
                        .name(contact.getName())
                        .email(contact.getEmail())
                        .message(contact.getMessage())
                        .phone(contact.getPhone())
                        .created(contact.getCreated())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.successResponse(response,"Contact Us fetched successfully");
    }

    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> getByPhone(String phone) throws GenericException {
        List<ContactUs> contacts = contactUsRepository.findByPhone(phone);
        if(contacts == null){
            return  ApiResponse.failureResponse(null,"Contact Us data not present");
        }
        List<ContactUsResponse> response = contacts.stream()
                .map(contact -> ContactUsResponse.builder()
                        .id(contact.getId())
                        .name(contact.getName())
                        .email(contact.getEmail())
                        .message(contact.getMessage())
                        .created(contact.getCreated())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.successResponse(response,"Contact Us fetched successfully");
    }

    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> getAllContactUs() throws GenericException {
        List<ContactUs> contacts = contactUsRepository.findAll();
        if(contacts == null){
            return ApiResponse.failureResponse(null,"Contact Us data not present");
        }
        List<ContactUsResponse> response = contacts.stream()
                .map(contact -> ContactUsResponse.builder()
                        .id(contact.getId())
                        .name(contact.getName())
                        .email(contact.getEmail())
                        .phone(contact.getPhone())
                        .message(contact.getMessage())
                        .created(contact.getCreated())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.successResponse(response,"Contact Us fetched successfully");
    }
}
