package com.portfolio.security;

import com.portfolio.entities.Profile;
import com.portfolio.repositories.ProfileRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    public CustomUserDetailsService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile profile = profileRepository.findByEmail(email);
        if (profile == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        return User.builder()
                .username(profile.getEmail())
                .password(profile.getPassword())
                .roles(profile.getRole()) // e.g., "ADMIN"
                .build();
    }
}
