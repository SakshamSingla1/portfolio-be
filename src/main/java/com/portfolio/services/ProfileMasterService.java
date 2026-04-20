package com.portfolio.services;

import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.exceptions.GenericException;

public interface ProfileMasterService {
    ProfileMasterResponse getProfileMasterData(String host) throws GenericException;
}
