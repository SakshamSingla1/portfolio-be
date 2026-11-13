package com.portfolio.dao.contact_message;

import com.portfolio.entities.ContactMessage;
import com.portfolio.repositories.ContactMessageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContactMessageDao {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageDao(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public ContactMessage save(ContactMessage message) {
        return contactMessageRepository.save(message);
    }

    public List<ContactMessage> findByContactUsId(Long contactUsId) {
        return contactMessageRepository.findByContactUsIdOrderByCreatedAtAsc(contactUsId);
    }
}
