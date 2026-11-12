package com.portfolio.servicesImpl;

import com.portfolio.dao.portfolio_view.PortfolioViewDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.entities.Profile;
import com.portfolio.repositories.ContactUsRepository;
import com.portfolio.services.NTService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * WeeklyDigestSchedulerTest -- guards against the digest's per-profile data fetch regressing back
 * to loading every raw PortfolioView row for the week (portfolioViewDao.findByProfileIdAndTimestampAfter)
 * just to count/group it in Java, the way it originally did. That pattern is fine for one profile
 * but scales badly across a whole digest run on a high-traffic profile base, so this asserts the
 * scheduler only ever calls the aggregate (COUNT/GROUP BY) DAO methods.
 */
class WeeklyDigestSchedulerTest {

    private final ProfileDao profileDao = mock(ProfileDao.class);
    private final PortfolioViewDao portfolioViewDao = mock(PortfolioViewDao.class);
    private final ContactUsRepository contactUsRepository = mock(ContactUsRepository.class);
    private final NTService ntService = mock(NTService.class);

    private final WeeklyDigestScheduler scheduler =
            new WeeklyDigestScheduler(profileDao, portfolioViewDao, contactUsRepository, ntService);

    @Test
    void sendWeeklyDigests_usesAggregateQueries_neverLoadsRawViewRows() throws Exception {
        Profile profile = Profile.builder().id(1L).fullName("Jane Doe").email("jane@example.com").build();
        when(profileDao.findAllDigestEnabled()).thenReturn(List.of(profile));
        when(portfolioViewDao.countByProfileIdAndTimestampBetween(eq(1L), any(), any())).thenReturn(42L);
        when(contactUsRepository.countByProfileIdAndCreatedAtAfter(eq(1L), any())).thenReturn(3L);
        when(portfolioViewDao.getReferrerBreakdownBetween(eq(1L), any(), any())).thenReturn(List.of(
                new Object[]{"github.com", 5L},
                new Object[]{"Direct", 20L},
                new Object[]{"linkedin.com", 10L},
                new Object[]{"twitter.com", 1L}
        ));

        scheduler.sendWeeklyDigests();

        // The whole point of this fix: never fall back to pulling raw rows to count/group in Java.
        verify(portfolioViewDao, never()).findByProfileIdAndTimestampAfter(anyLong(), any());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(ntService).sendNotification(eq("WEEKLY-DIGEST"), variablesCaptor.capture(), eq("jane@example.com"));

        Map<String, Object> variables = variablesCaptor.getValue();
        assertThat(variables.get("viewsThisWeek")).isEqualTo(42L);
        assertThat(variables.get("newMessages")).isEqualTo(3L);
        // Top 3 by count, descending: Direct(20), linkedin.com(10), github.com(5) -- twitter.com(1) dropped.
        String html = (String) variables.get("topReferrersHtml");
        assertThat(html).containsSubsequence("Direct", "linkedin.com", "github.com");
        assertThat(html).doesNotContain("twitter.com");

        verify(profileDao).save(argThat(p -> p.getDigestLastSentAt() != null));
    }

    @Test
    void sendWeeklyDigests_oneProfileFailing_stillProcessesTheRest() throws Exception {
        Profile failing = Profile.builder().id(1L).fullName("Broken").email("broken@example.com").build();
        Profile healthy = Profile.builder().id(2L).fullName("Fine").email("fine@example.com").build();
        when(profileDao.findAllDigestEnabled()).thenReturn(List.of(failing, healthy));
        when(portfolioViewDao.countByProfileIdAndTimestampBetween(anyLong(), any(), any())).thenReturn(0L);
        when(portfolioViewDao.getReferrerBreakdownBetween(anyLong(), any(), any())).thenReturn(List.of());
        when(contactUsRepository.countByProfileIdAndCreatedAtAfter(eq(1L), any()))
                .thenThrow(new RuntimeException("DB hiccup"));
        when(contactUsRepository.countByProfileIdAndCreatedAtAfter(eq(2L), any())).thenReturn(0L);

        scheduler.sendWeeklyDigests();

        verify(ntService, never()).sendNotification(any(), any(), eq("broken@example.com"));
        verify(ntService).sendNotification(any(), any(), eq("fine@example.com"));
        verify(profileDao, never()).save(argThat(p -> p.getId().equals(1L)));
        verify(profileDao).save(argThat(p -> p.getId().equals(2L)));
    }
}
