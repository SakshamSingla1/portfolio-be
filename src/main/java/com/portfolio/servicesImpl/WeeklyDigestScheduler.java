package com.portfolio.servicesImpl;

import com.portfolio.dao.portfolio_view.PortfolioViewDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.entities.PortfolioView;
import com.portfolio.entities.Profile;
import com.portfolio.repositories.ContactUsRepository;
import com.portfolio.services.EmailService;
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
    private final EmailService emailService;

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

        String html = buildEmailHtml(profile.getFullName(), viewsThisWeek, newMessages, top3);
        emailService.sendEmail(profile.getEmail(), "Your Portfolio — Weekly Summary", html);
    }

    private String buildEmailHtml(String name, long views, long messages,
                                  List<Map.Entry<String, Long>> referrers) {
        StringBuilder referrerRows = new StringBuilder();
        if (referrers.isEmpty()) {
            referrerRows.append("<tr><td colspan=\"2\" style=\"color:#6b7280;padding:8px 0\">No referrer data this week</td></tr>");
        } else {
            for (Map.Entry<String, Long> e : referrers) {
                referrerRows.append("<tr>")
                        .append("<td style=\"padding:6px 0;color:#374151\">").append(e.getKey()).append("</td>")
                        .append("<td style=\"padding:6px 0;color:#374151;font-weight:600\">").append(e.getValue()).append("</td>")
                        .append("</tr>");
            }
        }

        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="margin:0;padding:0;background:#f3f4f6;font-family:sans-serif">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 16px">
                    <tr><td align="center">
                      <table width="560" cellpadding="0" cellspacing="0"
                             style="background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,.08)">
                        <tr>
                          <td style="background:#4f46e5;padding:32px;text-align:center">
                            <p style="color:#c7d2fe;font-size:12px;margin:0 0 8px">WEEKLY PORTFOLIO DIGEST</p>
                            <h1 style="color:#fff;font-size:22px;margin:0">Hi, %s 👋</h1>
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:32px">
                            <p style="color:#374151;margin:0 0 24px">Here's what happened on your portfolio this week:</p>

                            <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:24px">
                              <tr>
                                <td width="50%%" style="padding:16px;text-align:center;background:#f9fafb;border-radius:8px;border:1px solid #e5e7eb">
                                  <p style="font-size:32px;font-weight:700;color:#4f46e5;margin:0">%d</p>
                                  <p style="font-size:13px;color:#6b7280;margin:4px 0 0">Portfolio Views</p>
                                </td>
                                <td width="4%%"></td>
                                <td width="50%%" style="padding:16px;text-align:center;background:#f9fafb;border-radius:8px;border:1px solid #e5e7eb">
                                  <p style="font-size:32px;font-weight:700;color:#4f46e5;margin:0">%d</p>
                                  <p style="font-size:13px;color:#6b7280;margin:4px 0 0">New Messages</p>
                                </td>
                              </tr>
                            </table>

                            <h3 style="font-size:14px;color:#374151;margin:0 0 12px">Top Traffic Sources</h3>
                            <table width="100%%" cellpadding="0" cellspacing="0"
                                   style="border-top:1px solid #e5e7eb;margin-bottom:24px">
                              %s
                            </table>

                            <p style="color:#9ca3af;font-size:12px;margin:32px 0 0;text-align:center">
                              To stop receiving these emails, turn off Weekly Digest in your admin profile settings.
                            </p>
                          </td>
                        </tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(name, views, messages, referrerRows.toString());
    }
}
