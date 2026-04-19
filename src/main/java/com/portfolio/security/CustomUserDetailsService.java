package com.portfolio.security;

import com.portfolio.entities.Profile;
import com.portfolio.entities.Role;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        String roleName = "USER"; // Default role
        if (profile.getRoleId() != null && !profile.getRoleId().isBlank()) {
            Role role = roleRepository.findById(profile.getRoleId()).orElse(null);
            if (role != null && role.getName() != null) {
                roleName = role.getName();
            }
        }
        
        return User.builder()
                .username(profile.getEmail())
                .password(profile.getPassword())
                .roles(roleName)
                .build();
    }
}
