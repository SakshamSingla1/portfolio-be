package com.portfolio.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.enums.ContactUsStatusEnum;
import com.portfolio.exceptions.GenericException;

public interface ContactUsService {
    public ContactUsResponse create(ContactUsRequest request) throws GenericException;
    public Page<ContactUsResponse> getContactUsByProfileId(Long profileId, String search, ContactUsStatusEnum status,Pageable pageable) throws GenericException;
    public void updateStatus(Long id, ContactUsStatusEnum status) throws GenericException;
    public ContactUsResponse reply(Long id, String replyMessage, String authHeader) throws GenericException;
}