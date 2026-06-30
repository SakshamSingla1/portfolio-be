package com.portfolio.dao.contact_us;

import com.portfolio.dtos.ContactUs.ContactUsResponse;
import com.portfolio.entities.ContactUs;
import com.portfolio.enums.ContactUsStatusEnum;
import com.portfolio.repositories.ContactUsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ContactUsDao {

    private final ContactUsRepository contactUsRepository;

    public ContactUsDao(ContactUsRepository contactUsRepository) {
        this.contactUsRepository = contactUsRepository;
    }

    public ContactUs save(ContactUs contactUs) {
        return contactUsRepository.save(contactUs);
    }

    public Optional<ContactUs> findById(Long id) {
        return contactUsRepository.findById(id);
    }

    public void deleteById(Long id) {
        contactUsRepository.deleteById(id);
    }

    public Optional<ContactUsResponse> findDTOById(Long id) {
        return contactUsRepository.findDTOById(id);
    }

    public Page<ContactUsResponse> findByCriteria(Long profileId, String search, ContactUsStatusEnum status, Pageable pageable){
        return contactUsRepository.findByCriteria(profileId,search,status,pageable);
    }

    public void updateStatusById(Long id, ContactUsStatusEnum status) {
        contactUsRepository.updateStatusById(id, status);
    }

    public List<ContactUs> findTop5ByProfileIdOrderByCreatedAtDesc(Long profileId) {
        return contactUsRepository.findTop5ByProfileIdOrderByCreatedAtDesc(profileId);
    }

    public List<ContactUsResponse> findTop5DTOByProfileIdOrderByCreatedAtDesc(Long profileId) {
        return contactUsRepository.findTop5DTOByProfileIdOrderByCreatedAtDesc(profileId);
    }

    public long countByProfileId(Long profileId) {
        return contactUsRepository.countByProfileId(profileId);
    }

    public long countByStatusAndProfileId(ContactUsStatusEnum status, Long profileId) {
        return contactUsRepository.countByStatusAndProfileId(status, profileId);
    }

    public Optional<ContactUs> findTop1ByProfileIdOrderByCreatedAtDesc(Long profileId) {
        return contactUsRepository.findTop1ByProfileIdOrderByCreatedAtDesc(profileId);
    }

    public boolean existsById(Long id) {
        return contactUsRepository.existsById(id);
    }
}
