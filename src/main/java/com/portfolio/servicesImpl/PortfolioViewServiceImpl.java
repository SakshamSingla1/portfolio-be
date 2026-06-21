package com.portfolio.servicesImpl;

import com.portfolio.dtos.DashboardDTOs.DailyViewDTO;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewDTO;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewRequest;
import com.portfolio.dtos.DashboardDTOs.ViewStatsDTO;
import com.portfolio.entities.PortfolioView;
import com.portfolio.repositories.PortfolioViewRepository;
import com.portfolio.repositories.ResumeDownloadRepository;
import com.portfolio.services.PortfolioViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioViewServiceImpl implements PortfolioViewService {

    private final PortfolioViewRepository viewRepository;
    private final ResumeDownloadRepository resumeDownloadRepository;

    @Override
    public void trackView(PortfolioViewRequest request) {
        if (request.getProfileId() == null) return;

        PortfolioView view = PortfolioView.builder()
                .profileId(request.getProfileId())
                .sessionId(request.getSessionId())
                .device(normaliseDevice(request.getDevice()))
                .referrer(request.getReferrer())
                .timestamp(LocalDateTime.now())
                .build();

        viewRepository.save(view);
    }

    @Override
    public ViewStatsDTO getViewStats(Long profileId) {
        LocalDateTime now        = LocalDateTime.now();
        LocalDateTime startDay   = now.toLocalDate().atStartOfDay();
        LocalDateTime startWeek  = now.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime startMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();

        long totalViews    = viewRepository.countByProfileId(profileId);
        long viewsToday    = viewRepository.countByProfileIdAndTimestampBetween(profileId, startDay, now);
        long viewsThisWeek = viewRepository.countByProfileIdAndTimestampBetween(profileId, startWeek, now);
        long viewsThisMonth = viewRepository.countByProfileIdAndTimestampBetween(profileId, startMonth, now);

        List<PortfolioView> last30 = viewRepository.findByProfileIdAndTimestampAfter(profileId, now.minusDays(30));

        long uniqueVisitors = last30.stream()
                .map(PortfolioView::getSessionId)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        Map<String, Long> deviceBreakdown = last30.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getDevice() != null ? v.getDevice() : "DESKTOP",
                        Collectors.counting()
                ));

        // Ensure all three keys are present
        deviceBreakdown.putIfAbsent("DESKTOP", 0L);
        deviceBreakdown.putIfAbsent("MOBILE",  0L);
        deviceBreakdown.putIfAbsent("TABLET",  0L);

        List<PortfolioView> last7 = last30.stream()
                .filter(v -> v.getTimestamp() != null && v.getTimestamp().isAfter(now.minusDays(7)))
                .toList();

        List<DailyViewDTO> weeklyTrend = buildWeeklyTrend(last7, now);

        long resumeDownloads = resumeDownloadRepository.countByProfileId(profileId);

        List<PortfolioViewDTO> recentViews = viewRepository
                .findTop30ByProfileIdOrderByTimestampDesc(profileId)
                .stream()
                .map(this::mapToViewDTO)
                .toList();

        return ViewStatsDTO.builder()
                .totalViews(totalViews)
                .viewsToday(viewsToday)
                .viewsThisWeek(viewsThisWeek)
                .viewsThisMonth(viewsThisMonth)
                .uniqueVisitors(uniqueVisitors)
                .resumeDownloads(resumeDownloads)
                .weeklyTrend(weeklyTrend)
                .deviceBreakdown(deviceBreakdown)
                .recentViews(recentViews)
                .build();
    }

    private PortfolioViewDTO mapToViewDTO(PortfolioView view) {
        String sid = view.getSessionId();
        String shortSid = (sid != null && sid.length() >= 8) ? sid.substring(0, 8) : sid;
        return PortfolioViewDTO.builder()
                .device(view.getDevice() != null ? view.getDevice() : "DESKTOP")
                .referrer(cleanReferrer(view.getReferrer()))
                .timestamp(view.getTimestamp())
                .sessionId(shortSid)
                .build();
    }

    private String cleanReferrer(String referrer) {
        if (referrer == null || referrer.isBlank()) return "Direct";
        try {
            String host = referrer.replaceFirst("https?://", "").replaceFirst("/.*", "");
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (Exception e) {
            return "Direct";
        }
    }

    private List<DailyViewDTO> buildWeeklyTrend(List<PortfolioView> views, LocalDateTime now) {
        Map<LocalDate, Long> byDate = views.stream()
                .filter(v -> v.getTimestamp() != null)
                .collect(Collectors.groupingBy(
                        v -> v.getTimestamp().toLocalDate(),
                        Collectors.counting()
                ));

        List<DailyViewDTO> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date  = now.toLocalDate().minusDays(i);
            long count      = byDate.getOrDefault(date, 0L);
            String day      = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String dateStr  = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + " " + date.getDayOfMonth();
            trend.add(DailyViewDTO.builder().day(day).date(dateStr).count(count).build());
        }
        return trend;
    }

    private String normaliseDevice(String raw) {
        if (raw == null) return "DESKTOP";
        return switch (raw.toUpperCase()) {
            case "MOBILE"  -> "MOBILE";
            case "TABLET"  -> "TABLET";
            default        -> "DESKTOP";
        };
    }
}
