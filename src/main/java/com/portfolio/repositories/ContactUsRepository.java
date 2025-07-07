package com.portfolio.repositories;

import com.portfolio.entities.ContactUs;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactUsRepository extends CrudRepository<ContactUs, Integer> {
    List<ContactUs> findAll();
    List<ContactUs> findByName(String name);
    List<ContactUs> findByEmail(String email);
    List<ContactUs> findByPhone(String phone);
}
