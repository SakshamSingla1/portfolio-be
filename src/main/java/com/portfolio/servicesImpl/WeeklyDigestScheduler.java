package com.portfolio.servicesImpl;

import com.portfolio.dao.portfolio_view.PortfolioViewDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.entities.PortfolioView;
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
import java.util.stream.Collectors;

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
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<Profile> profiles = profileDao.findAllDigestEnabled();

        for (Profile profile : profiles) {
            try {
                sendDigest(profile, weekAgo);
                profile.setDigestLastSentAt(LocalDateTime.now());
                profileDao.save(profile);
            } catch (Exception e) {
                log.warn("Weekly digest failed for profile {}: {}", profile.getId(), e.getMessage());
            }
        }
        log.info("Weekly digest sent to {} profiles", profiles.size());
    }

    private void sendDigest(Profile profile, LocalDateTime weekAgo) {
        List<PortfolioView> weekViews = portfolioViewDao
                .findByProfileIdAndTimestampAfter(profile.getId(), weekAgo);

        long viewsThisWeek = weekViews.size();

        long newMessages = contactUsRepository
                .countByProfileIdAndCreatedAtAfter(profile.getId(), weekAgo);

        Map<String, Long> topReferrers = weekViews
                .stream()
                .map(v -> v.getReferrerDomain() != null ? v.getReferrerDomain() : "Direct")
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()));

        List<Map.Entry<String, Long>> top3 = topReferrers.entrySet().stream()
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
