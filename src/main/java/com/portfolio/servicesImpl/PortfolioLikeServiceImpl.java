package com.portfolio.servicesImpl;

import com.portfolio.dao.portfolio_like.PortfolioLikeDao;
import com.portfolio.dtos.Like.LikeStatusResponse;
import com.portfolio.entities.PortfolioLike;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.PortfolioLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PortfolioLikeServiceImpl implements PortfolioLikeService {

    private final PortfolioLikeDao portfolioLikeDao;

    @Override
    public LikeStatusResponse getStatus(Long profileId, String visitorId) throws GenericException {
        requireVisitorId(visitorId);
        boolean liked = portfolioLikeDao.findByProfileIdAndVisitorId(profileId, visitorId).isPresent();
        return new LikeStatusResponse(portfolioLikeDao.countByProfileId(profileId), liked);
    }

    @Override
    @Transactional
    public LikeStatusResponse toggleLike(Long profileId, String visitorId) throws GenericException {
        requireVisitorId(visitorId);
        boolean alreadyLiked = portfolioLikeDao.findByProfileIdAndVisitorId(profileId, visitorId).isPresent();
        if (alreadyLiked) {
            portfolioLikeDao.deleteByProfileIdAndVisitorId(profileId, visitorId);
        } else {
            try {
                portfolioLikeDao.save(PortfolioLike.builder().profileId(profileId).visitorId(visitorId).build());
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                // Two near-simultaneous toggles from the same visitor (double-click, retry) --
                // the unique constraint already caught it, so this is already "liked"; nothing
                // left to do.
            }
        }
        return new LikeStatusResponse(portfolioLikeDao.countByProfileId(profileId), !alreadyLiked);
    }

    private void requireVisitorId(String visitorId) throws GenericException {
        if (visitorId == null || visitorId.isBlank()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "visitorId is required");
        }
    }
}
