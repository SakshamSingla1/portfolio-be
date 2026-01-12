package com.portfolio.services;

import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;

public interface ProfileService {
    ProfileResponse get(String id);
    ProfileResponse update(String id, ProfileRequest request);
}
