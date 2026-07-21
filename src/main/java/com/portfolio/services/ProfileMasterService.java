package com.portfolio.services;

import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.exceptions.GenericException;

public interface ProfileMasterService {
    ProfileMasterResponse getProfileMasterData(String host) throws GenericException;
    ProfileMasterResponse getByProfileId(Long profileId) throws GenericException;

    /**
     * Lean variant for the resume PDF export — skips GitHub stats/repos (which can trigger a
     * live external API call when nothing is cached), testimonials, and SEO meta, since none of
     * those are rendered in the exported PDF.
     */
    ProfileMasterResponse getForResumeExport(Long profileId) throws GenericException;
}
