package com.portfolio.servicesImpl;

import com.portfolio.dao.portfolio_view.PortfolioViewDao;
import com.portfolio.dao.resume.ResumeDownloadDao;
import com.portfolio.dtos.DashboardDTOs.AnalyticsComparisonDTO;
import com.portfolio.dtos.DashboardDTOs.AnalyticsOverviewDTO;
import com.portfolio.dtos.DashboardDTOs.AnalyticsRangeDTO;
import com.portfolio.dtos.DashboardDTOs.DailyViewDTO;
import com.portfolio.dtos.DashboardDTOs.MetricComparisonDTO;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewDTO;
import com.portfolio.dtos.DashboardDTOs.PortfolioViewRequest;
import com.portfolio.dtos.DashboardDTOs.ViewStatsDTO;
import com.portfolio.entities.PortfolioView;
import com.portfolio.services.PortfolioViewService;
import com.portfolio.utils.AnalyticsRangeResolver;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioViewServiceImpl implements PortfolioViewService {

    private final PortfolioViewDao portfolioViewDao;
    private final ResumeDownloadDao resumeDownloadDao;
    private final RestTemplate restTemplate;

    @Override
    public void trackView(PortfolioViewRequest request, String clientIp, String userAgent) {
        if (request.getProfileId() == null) return;

        GeoLocation geo = fetchGeoLocation(clientIp);

        String normDevice = normaliseDevice(request.getDevice());
        String refUrl    = request.getReferrer();
        String refDomain = cleanReferrer(refUrl);

        PortfolioView view = PortfolioView.builder()
                .profileId(request.getProfileId())
                .sessionId(request.getSessionId())
                .device(normDevice)
                .deviceType(normDevice)
                .referrer(refUrl)
                .referrerUrl(refUrl != null && !refUrl.isBlank() ? refUrl : null)
                .referrerDomain("Direct".equals(refDomain) ? null : refDomain)
                .browser(request.getBrowser())
                .os(request.getOs())
                .language(request.getLanguage())
                .timezone(request.getTimezone())
                .country(geo != null ? geo.getCountry() : null)
                .city(geo != null ? geo.getCity() : null)
                .countryCode(geo != null ? geo.getCountryCode() : null)
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        portfolioViewDao.save(view);
    }

    @Override
    public ViewStatsDTO getViewStats(Long profileId) {
        LocalDateTime now         = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime startDay    = now.toLocalDate().atStartOfDay();
        LocalDateTime startWeek   = now.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime startMonth  = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startLastWeek = startWeek.minusDays(7);

        // 3 queries in parallel instead of 8 sequential queries.
        // minusDays(32) covers startLastWeek (up to 13 days ago) and startMonth (up to 31 days ago).
        CompletableFuture<Long> totalViewsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.countByProfileId(profileId));
        CompletableFuture<Long> resumeDownloadsFuture = CompletableFuture.supplyAsync(
                () -> resumeDownloadDao.countByProfileId(profileId));
        CompletableFuture<List<PortfolioView>> recentFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.findByProfileIdAndTimestampAfter(profileId, now.minusDays(32)));
        // Grouped in SQL (not pulled as raw rows like `recent`) since 90 days of
        // history on a high-traffic profile would be a much larger row set.
        CompletableFuture<List<Object[]>> heatmapRowsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.getDailyViewCountsBetween(profileId, now.minusDays(89).toLocalDate().atStartOfDay(), now));

        CompletableFuture.allOf(totalViewsFuture, resumeDownloadsFuture, recentFuture, heatmapRowsFuture).join();

        long totalViews      = totalViewsFuture.join();
        long resumeDownloads = resumeDownloadsFuture.join();
        List<PortfolioView> recent = recentFuture.join();
        List<Object[]> heatmapRows = heatmapRowsFuture.join();

        // Compute date-range counts in Java — no extra DB round trips needed
        long viewsToday     = recent.stream().filter(v -> v.getTimestamp() != null && !v.getTimestamp().isBefore(startDay)).count();
        long viewsThisWeek  = recent.stream().filter(v -> v.getTimestamp() != null && !v.getTimestamp().isBefore(startWeek)).count();
        long viewsLastWeek  = recent.stream().filter(v -> v.getTimestamp() != null && !v.getTimestamp().isBefore(startLastWeek) && v.getTimestamp().isBefore(startWeek)).count();
        long viewsThisMonth = recent.stream().filter(v -> v.getTimestamp() != null && !v.getTimestamp().isBefore(startMonth)).count();

        List<PortfolioView> last30 = recent.stream()
                .filter(v -> v.getTimestamp() != null && v.getTimestamp().isAfter(now.minusDays(30)))
                .toList();

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
        deviceBreakdown.putIfAbsent("DESKTOP", 0L);
        deviceBreakdown.putIfAbsent("MOBILE",  0L);
        deviceBreakdown.putIfAbsent("TABLET",  0L);

        Map<String, Long> browserBreakdown = last30.stream()
                .filter(v -> v.getBrowser() != null && !v.getBrowser().isBlank())
                .collect(Collectors.groupingBy(PortfolioView::getBrowser, Collectors.counting()));

        Map<String, Long> locationBreakdown = last30.stream()
                .filter(v -> v.getCountry() != null && !v.getCountry().isBlank())
                .collect(Collectors.groupingBy(PortfolioView::getCountry, Collectors.counting()));

        Map<String, Long> referrerBreakdown = last30.stream()
                .collect(Collectors.groupingBy(
                        v -> {
                            String domain = cleanReferrer(v.getReferrer());
                            return domain.isBlank() ? "Direct" : domain;
                        },
                        Collectors.counting()
                ));

        List<PortfolioView> last7 = last30.stream()
                .filter(v -> v.getTimestamp().isAfter(now.minusDays(7)))
                .toList();

        List<DailyViewDTO> weeklyTrend = buildWeeklyTrend(last7, now);
        List<DailyViewDTO> viewsHeatmap = buildHeatmap(heatmapRows, now);

        List<PortfolioViewDTO> recentViews = recent.stream()
                .filter(v -> v.getTimestamp() != null)
                .sorted(Comparator.comparing(PortfolioView::getTimestamp).reversed())
                .limit(30)
                .map(this::mapToViewDTO)
                .toList();

        return ViewStatsDTO.builder()
                .totalViews(totalViews)
                .viewsToday(viewsToday)
                .viewsThisWeek(viewsThisWeek)
                .viewsLastWeek(viewsLastWeek)
                .viewsThisMonth(viewsThisMonth)
                .uniqueVisitors(uniqueVisitors)
                .resumeDownloads(resumeDownloads)
                .weeklyTrend(weeklyTrend)
                .viewsHeatmap(viewsHeatmap)
                .deviceBreakdown(deviceBreakdown)
                .browserBreakdown(browserBreakdown)
                .locationBreakdown(locationBreakdown)
                .referrerBreakdown(referrerBreakdown)
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
                .browser(view.getBrowser())
                .os(view.getOs())
                .language(view.getLanguage())
                .timezone(view.getTimezone())
                .country(view.getCountry())
                .city(view.getCity())
                .countryCode(view.getCountryCode())
                .build();
    }

    private GeoLocation fetchGeoLocation(String ip) {
        if (ip == null || ip.isBlank()) return null;
        if ("127.0.0.1".equals(ip) || "::1".equals(ip)) return null;
        if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) return null;
        try {
            // NOTE: ip-api.com HTTPS endpoint requires a Pro/paid plan key.
            String url = "https://ip-api.com/json/" + ip + "?fields=status,country,countryCode,city";
            GeoLocation result = restTemplate.getForObject(url, GeoLocation.class);
            return (result != null && "success".equals(result.getStatus())) ? result : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Data
    private static class GeoLocation {
        private String status;
        private String country;
        private String countryCode;
        private String city;
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

    private List<DailyViewDTO> buildHeatmap(List<Object[]> rows, LocalDateTime now) {
        Map<LocalDate, Long> byDate = new HashMap<>();
        for (Object[] row : rows) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            long count = ((Number) row[1]).longValue();
            byDate.put(date, count);
        }

        List<DailyViewDTO> heatmap = new ArrayList<>();
        for (int i = 89; i >= 0; i--) {
            LocalDate date  = now.toLocalDate().minusDays(i);
            long count      = byDate.getOrDefault(date, 0L);
            String day      = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String dateStr  = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + " " + date.getDayOfMonth();
            heatmap.add(DailyViewDTO.builder().day(day).date(dateStr).count(count).build());
        }
        return heatmap;
    }

    private String normaliseDevice(String raw) {
        if (raw == null) return "DESKTOP";
        return switch (raw.toUpperCase()) {
            case "MOBILE" -> "MOBILE";
            case "TABLET" -> "TABLET";
            default       -> "DESKTOP";
        };
    }

    // ── Analytics Dashboard: date-range + period comparison ────────────────────
    // Purely additive — getViewStats(Long) above is untouched (still powers the home
    // Dashboard's fixed-window widget) except for the getDailyViewCountsBetween rename.

    @Override
    public AnalyticsOverviewDTO getViewStatsForRange(Long profileId, String rangeKey, String startDateParam, String endDateParam) {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve(rangeKey, startDateParam, endDateParam);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Current-period queries
        CompletableFuture<Long> viewsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.countByProfileIdAndTimestampBetween(profileId, range.start(), range.end()));
        CompletableFuture<Long> uniqueVisitorsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.countDistinctSessionIdByProfileIdAndTimestampBetween(profileId, range.start(), range.end()));
        CompletableFuture<Long> resumeDownloadsFuture = CompletableFuture.supplyAsync(
                () -> resumeDownloadDao.countByProfileIdAndDownloadedAtBetween(profileId, range.start(), range.end()));
        CompletableFuture<List<Object[]>> trendRowsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.getDailyViewCountsBetween(profileId, range.start(), range.end()));
        CompletableFuture<List<Object[]>> deviceRowsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.getDeviceBreakdownBetween(profileId, range.start(), range.end()));
        CompletableFuture<List<Object[]>> browserRowsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.getBrowserBreakdownBetween(profileId, range.start(), range.end()));
        CompletableFuture<List<Object[]>> locationRowsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.getLocationBreakdownBetween(profileId, range.start(), range.end()));
        CompletableFuture<List<Object[]>> referrerRowsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.getReferrerBreakdownBetween(profileId, range.start(), range.end()));
        CompletableFuture<List<PortfolioView>> recentViewsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.findTop200ByProfileIdAndTimestampBetweenOrderByTimestampDesc(profileId, range.start(), range.end()));

        // Previous-period queries — totals only, for the comparison block.
        CompletableFuture<Long> prevViewsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.countByProfileIdAndTimestampBetween(profileId, range.prevStart(), range.prevEnd()));
        CompletableFuture<Long> prevUniqueVisitorsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.countDistinctSessionIdByProfileIdAndTimestampBetween(profileId, range.prevStart(), range.prevEnd()));
        CompletableFuture<Long> prevResumeDownloadsFuture = CompletableFuture.supplyAsync(
                () -> resumeDownloadDao.countByProfileIdAndDownloadedAtBetween(profileId, range.prevStart(), range.prevEnd()));

        // Fixed trailing-90-day heatmap, independent of the selected range.
        CompletableFuture<List<Object[]>> heatmapRowsFuture = CompletableFuture.supplyAsync(
                () -> portfolioViewDao.getDailyViewCountsBetween(profileId, now.minusDays(89).toLocalDate().atStartOfDay(), now));

        CompletableFuture.allOf(
                viewsFuture, uniqueVisitorsFuture, resumeDownloadsFuture, trendRowsFuture,
                deviceRowsFuture, browserRowsFuture, locationRowsFuture, referrerRowsFuture, recentViewsFuture,
                prevViewsFuture, prevUniqueVisitorsFuture, prevResumeDownloadsFuture, heatmapRowsFuture
        ).join();

        AnalyticsComparisonDTO comparison = AnalyticsComparisonDTO.builder()
                .views(computeComparison(viewsFuture.join(), prevViewsFuture.join()))
                .uniqueVisitors(computeComparison(uniqueVisitorsFuture.join(), prevUniqueVisitorsFuture.join()))
                .resumeDownloads(computeComparison(resumeDownloadsFuture.join(), prevResumeDownloadsFuture.join()))
                .build();

        AnalyticsRangeDTO rangeDTO = AnalyticsRangeDTO.builder()
                .key(range.key())
                .startDate(range.startDate())
                .endDate(range.endDate())
                .label(range.label())
                .build();

        List<PortfolioViewDTO> recentViews = recentViewsFuture.join().stream()
                .map(this::mapToViewDTO)
                .toList();

        return AnalyticsOverviewDTO.builder()
                .range(rangeDTO)
                .comparison(comparison)
                .trend(buildTrend(trendRowsFuture.join(), range.startDate(), range.endDate()))
                .deviceBreakdown(toLongMap(deviceRowsFuture.join()))
                .browserBreakdown(toLongMap(browserRowsFuture.join()))
                .locationBreakdown(toLongMap(locationRowsFuture.join()))
                .referrerBreakdown(toLongMap(referrerRowsFuture.join()))
                .recentViews(recentViews)
                .viewsHeatmap(buildTrend(heatmapRowsFuture.join(), now.minusDays(89).toLocalDate(), now.toLocalDate()))
                .build();
    }

    // Null when `previous` is 0 — see MetricComparisonDTO's javadoc for why that's treated
    // as "no comparison available" rather than a misleading +Infinity%.
    // Package-private (not private) so PortfolioViewServiceImplTest can exercise it directly.
    MetricComparisonDTO computeComparison(long current, long previous) {
        Double percentChange = previous == 0
                ? null
                : Math.round(((double) (current - previous) / previous) * 1000.0) / 10.0;
        return MetricComparisonDTO.builder()
                .current(current)
                .previous(previous)
                .percentChange(percentChange)
                .build();
    }

    private Map<String, Long> toLongMap(List<Object[]> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return map;
    }

    // Generalizes buildWeeklyTrend/buildHeatmap's zero-fill-per-day loop to an arbitrary
    // date span, for the Analytics Dashboard's range-aware trend chart (and its own
    // fixed-90-day heatmap call, kept separate from the legacy buildHeatmap above so that
    // getViewStats's existing behavior/call path is never touched by this feature).
    private List<DailyViewDTO> buildTrend(List<Object[]> rows, LocalDate start, LocalDate end) {
        Map<LocalDate, Long> byDate = new HashMap<>();
        for (Object[] row : rows) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            byDate.put(date, ((Number) row[1]).longValue());
        }

        List<DailyViewDTO> trend = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            long count = byDate.getOrDefault(date, 0L);
            String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String dateStr = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + date.getDayOfMonth();
            trend.add(DailyViewDTO.builder().day(day).date(dateStr).count(count).build());
        }
        return trend;
    }
}
