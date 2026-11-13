package com.portfolio.servicesImpl;

import com.portfolio.dao.portfolio_like.PortfolioLikeDao;
import com.portfolio.dtos.Like.LikeStatusResponse;
import com.portfolio.entities.PortfolioLike;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PortfolioLikeServiceImplTest {

    private final PortfolioLikeDao portfolioLikeDao = mock(PortfolioLikeDao.class);
    private final PortfolioLikeServiceImpl service = new PortfolioLikeServiceImpl(portfolioLikeDao);

    private static final long PROFILE_ID = 1L;
    private static final String VISITOR_ID = "visitor-abc";

    @Test
    void toggleLike_notYetLiked_likesAndIncrementsCount() throws Exception {
        when(portfolioLikeDao.findByProfileIdAndVisitorId(PROFILE_ID, VISITOR_ID)).thenReturn(Optional.empty());
        when(portfolioLikeDao.countByProfileId(PROFILE_ID)).thenReturn(5L);

        LikeStatusResponse result = service.toggleLike(PROFILE_ID, VISITOR_ID);

        verify(portfolioLikeDao).save(argThat(like -> like.getProfileId().equals(PROFILE_ID) && VISITOR_ID.equals(like.getVisitorId())));
        verify(portfolioLikeDao, never()).deleteByProfileIdAndVisitorId(any(), any());
        assertThat(result.isLikedByVisitor()).isTrue();
        assertThat(result.getTotalLikes()).isEqualTo(5L);
    }

    @Test
    void toggleLike_alreadyLiked_unlikesAndDecrementsCount() throws Exception {
        when(portfolioLikeDao.findByProfileIdAndVisitorId(PROFILE_ID, VISITOR_ID))
                .thenReturn(Optional.of(PortfolioLike.builder().profileId(PROFILE_ID).visitorId(VISITOR_ID).build()));
        when(portfolioLikeDao.countByProfileId(PROFILE_ID)).thenReturn(4L);

        LikeStatusResponse result = service.toggleLike(PROFILE_ID, VISITOR_ID);

        verify(portfolioLikeDao).deleteByProfileIdAndVisitorId(PROFILE_ID, VISITOR_ID);
        verify(portfolioLikeDao, never()).save(any());
        assertThat(result.isLikedByVisitor()).isFalse();
        assertThat(result.getTotalLikes()).isEqualTo(4L);
    }

    @Test
    void toggleLike_blankVisitorId_rejected() {
        assertThatThrownBy(() -> service.toggleLike(PROFILE_ID, "  "))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.BAD_REQUEST);
        verifyNoInteractions(portfolioLikeDao);
    }

    @Test
    void getStatus_reflectsWhetherVisitorHasLiked() throws Exception {
        when(portfolioLikeDao.findByProfileIdAndVisitorId(PROFILE_ID, VISITOR_ID))
                .thenReturn(Optional.of(PortfolioLike.builder().build()));
        when(portfolioLikeDao.countByProfileId(PROFILE_ID)).thenReturn(10L);

        LikeStatusResponse result = service.getStatus(PROFILE_ID, VISITOR_ID);

        assertThat(result.isLikedByVisitor()).isTrue();
        assertThat(result.getTotalLikes()).isEqualTo(10L);
    }
}
