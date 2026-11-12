package com.portfolio.servicesImpl;

import com.portfolio.dao.portfolio_view.PortfolioViewDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.entities.Profile;
import com.portfolio.repositories.ContactUsRepository;
import com.portfolio.services.NTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyDigestScheduler {

    private final ProfileDao profileDao;
    private final PortfolioViewDao portfolioViewDao;
    private final ContactUsRepository contactUsRepository;
    private final NTService ntService;

    // Every Monday at 9:00 AM
    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklyDigests() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        List<Profile> profiles = profileDao.findAllDigestEnabled();

        for (Profile profile : profiles) {
            try {
                sendDigest(profile, weekAgo, now);
                profile.setDigestLastSentAt(LocalDateTime.now());
                profileDao.save(profile);
            } catch (Exception e) {
                log.warn("Weekly digest failed for profile {}: {}", profile.getId(), e.getMessage());
            }
        }
        log.info("Weekly digest sent to {} profiles", profiles.size());
    }

    private void sendDigest(Profile profile, LocalDateTime weekAgo, LocalDateTime now) {
        // Aggregated in SQL rather than loading every raw PortfolioView row for the week just to
        // .size()/group them in Java -- same reasoning as PortfolioViewServiceImpl's dashboard
        // queries: on a high-traffic profile, a week of raw rows is a much larger (and more
        // memory-expensive) result set than a handful of grouped counts.
        long viewsThisWeek = portfolioViewDao.countByProfileIdAndTimestampBetween(profile.getId(), weekAgo, now);

        long newMessages = contactUsRepository
                .countByProfileIdAndCreatedAtAfter(profile.getId(), weekAgo);

        List<Object[]> referrerRows = portfolioViewDao.getReferrerBreakdownBetween(profile.getId(), weekAgo, now);
        List<Map.Entry<String, Long>> top3 = referrerRows.stream()
                .map(row -> Map.entry((String) row[0], ((Number) row[1]).longValue()))
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .toList();

        String referrerRowsHtml = buildReferrerRowsHtml(top3);
        try {
            ntService.sendNotification(
                    "WEEKLY-DIGEST",
                    Map.of(
                            "fullName", profile.getFullName(),
                            "viewsThisWeek", viewsThisWeek,
                            "newMessages", newMessages,
                            "topReferrersHtml", referrerRowsHtml
                    ),
                    profile.getEmail()
            );
        } catch (Exception e) {
            log.warn("Failed to send weekly digest email for profile {}: {}", profile.getId(), e.getMessage());
        }
    }

    private String buildReferrerRowsHtml(List<Map.Entry<String, Long>> referrers) {
        if (referrers.isEmpty()) {
            return "<tr><td colspan=\"2\" style=\"color:#6b7280;padding:8px 0\">No referrer data this week</td></tr>";
        }
        StringBuilder referrerRows = new StringBuilder();
        for (Map.Entry<String, Long> e : referrers) {
            referrerRows.append("<tr>")
                    .append("<td style=\"padding:6px 0;color:#374151\">").append(e.getKey()).append("</td>")
                    .append("<td style=\"padding:6px 0;color:#374151;font-weight:600\">").append(e.getValue()).append("</td>")
                    .append("</tr>");
        }
        return referrerRows.toString();
    }
}
