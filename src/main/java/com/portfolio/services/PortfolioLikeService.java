package com.portfolio.services;

import com.portfolio.dtos.Like.LikeStatusResponse;
import com.portfolio.exceptions.GenericException;

public interface PortfolioLikeService {

    LikeStatusResponse getStatus(Long profileId, String visitorId) throws GenericException;

    /** Likes if {@code visitorId} hasn't already liked this profile, unlikes otherwise. */
    LikeStatusResponse toggleLike(Long profileId, String visitorId) throws GenericException;
}
