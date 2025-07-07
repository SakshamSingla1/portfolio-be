package com.portfolio.repositories;

import com.portfolio.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Profile findByEmail(String email);
    Profile findByPhone(String phone);
    Profile findByEmailAndPhone(String email, String phone);
}
