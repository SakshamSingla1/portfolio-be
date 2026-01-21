package com.portfolio.services;

import com.portfolio.dtos.SocialLinks.SocialLinkRequestDTO;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SocialLinkService {
    SocialLinkResponseDTO createLink(SocialLinkRequestDTO requestDTO) throws GenericException;
    SocialLinkResponseDTO updateLink(String id,SocialLinkRequestDTO requestDTO) throws GenericException;
    List<SocialLinkResponseDTO> getAllSocialLinks(String profileId);
    Page<SocialLinkResponseDTO> getByProfile(String profileId, StatusEnum status, Pageable pageable, String search, String sortDir, String sortBy);
    SocialLinkResponseDTO get(String id) throws GenericException;
    void delete(String id) throws GenericException;
}
