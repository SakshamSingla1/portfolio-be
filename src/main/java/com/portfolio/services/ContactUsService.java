package com.portfolio.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.portfolio.dtos.ContactUs.ContactMessageResponse;
import com.portfolio.dtos.ContactUs.ContactThreadResponse;
import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.enums.ContactUsStatusEnum;
import com.portfolio.exceptions.GenericException;

import java.util.List;

public interface ContactUsService {
    public ContactUsResponse create(ContactUsRequest request) throws GenericException;
    public Page<ContactUsResponse> getContactUsByProfileId(Long profileId, String search, ContactUsStatusEnum status,Pageable pageable) throws GenericException;
    public void updateStatus(Long id, ContactUsStatusEnum status) throws GenericException;
    public int updateStatusBulk(List<Long> ids, ContactUsStatusEnum status) throws GenericException;
    public ContactUsResponse reply(Long id, String replyMessage, String authHeader) throws GenericException;
    public void deleteContactUs(Long id, Long profileId) throws GenericException;

    /** Owner's view of the full thread (original message + every reply), scoped to their own profile. */
    ContactThreadResponse getThreadForOwner(Long contactUsId, Long profileId) throws GenericException;

    /** Visitor's view of the same thread, resolved by the magic-link token instead of auth. */
    ContactThreadResponse getThreadByToken(String token) throws GenericException;

    /** Visitor replying via the token link -- appends a message and notifies the owner. */
    ContactMessageResponse addVisitorReply(String token, String message) throws GenericException;
}