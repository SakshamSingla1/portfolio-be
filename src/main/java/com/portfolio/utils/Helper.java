package com.portfolio.utils;

import com.portfolio.audit.Auditable;
import com.portfolio.dtos.AuditableResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.repositories.ProfileRepository;
import com.portfolio.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class Helper {

    private final JwtUtil jwtUtil;
    private final ProfileRepository profileRepository;

    private static final String SYSTEM = "SYSTEM";
    private static final String UNKNOWN = "Unknown";

    public String extractEmailFromHeader(String header) throws GenericException {
        if (header != null && header.startsWith("Bearer ")) {
            return jwtUtil.extractEmail(header.substring(7));
        }
        return getAuthenticatedEmail();
    }

    public Profile getProfileFromHeader(String header) throws GenericException {
        if (header != null && header.startsWith("Bearer ")) {
            String email = jwtUtil.extractEmail(header.substring(7));
            if (email != null) {
                return profileRepository.findByEmail(email)
                        .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
            }
        }
        return getAuthenticatedProfile();
    }

    public Long getProfileIdFromHeader(String header) throws GenericException {
        if (header != null && header.startsWith("Bearer ")) {
            String userId = jwtUtil.extractUserId(header.substring(7));
            if (userId != null) return Long.parseLong(userId);
        }
        return getAuthenticatedProfileId();
    }

    public Long getAuthenticatedProfileId() throws GenericException {
        return getAuthenticatedProfile().getId();
    }

    public Profile getAuthenticatedProfile() throws GenericException {
        String email = getAuthenticatedEmail();
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "User not found"));
    }

    private String getAuthenticatedEmail() throws GenericException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            throw new GenericException(ExceptionCodeEnum.UNAUTHORIZED, "Not authenticated");
        }
        return auth.getName();
    }

    public String generateRawOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

    public void setAudit(Auditable entity, AuditableResponse dto) {
        if (entity == null || dto == null) return;

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        dto.setCreatedByName(resolveUsername(entity.getCreatedBy()));
        dto.setUpdatedByName(resolveUsername(entity.getUpdatedBy()));
    }

    public Map<Long, String> prepareUserMap(Collection<? extends Auditable> entities) {
        if (entities == null || entities.isEmpty()) return Collections.emptyMap();

        Set<Long> ids = entities.stream()
                .flatMap(e -> Stream.of(e.getCreatedBy(), e.getUpdatedBy()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ids.isEmpty()) return Collections.emptyMap();

        return profileRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(
                        Profile::getId,
                        Profile::getFullName
                ));
    }

    public void setAudit(
            Auditable entity,
            AuditableResponse dto,
            Map<Long, String> userMap
    ) {
        if (entity == null || dto == null) return;

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        dto.setCreatedByName(resolveFromMap(entity.getCreatedBy(), userMap));
        dto.setUpdatedByName(resolveFromMap(entity.getUpdatedBy(), userMap));
    }

    public <E extends Auditable, D extends AuditableResponse> List<D> mapAuditList(
            List<E> entities,
            Function<E, D> dtoMapper
    ) {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();

        Map<Long, String> userMap = prepareUserMap(entities);

        return entities.stream()
                .map(entity -> {
                    D dto = dtoMapper.apply(entity);
                    setAudit(entity, dto, userMap);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String resolveUsername(Long id) {
        if (id == null) return SYSTEM;
        return profileRepository.findById(id)
                .map(Profile::getFullName)
                .orElse(UNKNOWN);
    }

    private String resolveFromMap(Long id, Map<Long, String> userMap) {
        if (id == null) return SYSTEM;
        return userMap.getOrDefault(id, UNKNOWN);
    }

    public List<Long> parseIds(String idsString) {
        if (idsString == null || idsString.isBlank()) return null;
        try {
            return Arrays.stream(idsString.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public List<StatusEnum> parseStatuses(String statusesString) {
        if (statusesString == null || statusesString.isBlank()) return null;
        try {
            return Arrays.stream(statusesString.split(","))
                    .map(String::trim)
                    .map(StatusEnum::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}