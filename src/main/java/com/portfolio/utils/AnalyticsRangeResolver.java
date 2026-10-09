package com.portfolio.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Resolves the Analytics Dashboard's {@code range}/{@code startDate}/{@code endDate} query
 * params into a concrete current period plus the immediately-preceding period of equal
 * length (for period-over-period comparison). A non-"custom" {@code range} always wins over
 * stray date params; anything missing/unparseable falls back to "30d" rather than rejecting
 * the request, matching this codebase's existing convention of silently defaulting on bad
 * query input (see {@code Helper.parseIds}).
 */
public final class AnalyticsRangeResolver {

    private static final String DEFAULT_KEY = "30d";
    private static final int MAX_SPAN_DAYS = 366;

    private AnalyticsRangeResolver() {
    }

    public record AnalyticsRange(
            String key,
            LocalDateTime start,
            LocalDateTime end,
            LocalDateTime prevStart,
            LocalDateTime prevEnd,
            LocalDate startDate,
            LocalDate endDate,
            String label
    ) {
    }

    public static AnalyticsRange resolve(String rangeKey, String startDateParam, String endDateParam) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        String key = normaliseKey(rangeKey);

        LocalDateTime start;
        LocalDateTime end;
        String label;

        if ("custom".equals(key)) {
            LocalDate parsedStart = parseDateSafe(startDateParam);
            LocalDate parsedEnd = parseDateSafe(endDateParam);

            if (parsedStart == null || parsedEnd == null || parsedStart.isAfter(parsedEnd)) {
                key = DEFAULT_KEY;
                start = fixedWindowStart(now, daysFor(key));
                end = now;
                label = labelFor(key);
            } else {
                start = capSpan(parsedStart, parsedEnd).atStartOfDay();
                end = parsedEnd.atTime(23, 59, 59);
                label = "Custom";
            }
        } else {
            start = fixedWindowStart(now, daysFor(key));
            end = now;
            label = labelFor(key);
        }

        Duration duration = Duration.between(start, end);
        LocalDateTime prevEnd = start;
        LocalDateTime prevStart = start.minus(duration);

        return new AnalyticsRange(key, start, end, prevStart, prevEnd, start.toLocalDate(), end.toLocalDate(), label);
    }

    private static LocalDateTime fixedWindowStart(LocalDateTime now, int days) {
        return now.toLocalDate().minusDays(days - 1L).atStartOfDay();
    }

    private static String normaliseKey(String rangeKey) {
        if (rangeKey == null) return DEFAULT_KEY;
        return switch (rangeKey) {
            case "7d", "30d", "90d", "custom" -> rangeKey;
            default -> DEFAULT_KEY;
        };
    }

    private static int daysFor(String key) {
        return switch (key) {
            case "7d" -> 7;
            case "90d" -> 90;
            default -> 30;
        };
    }

    private static String labelFor(String key) {
        return switch (key) {
            case "7d" -> "Last 7 days";
            case "90d" -> "Last 90 days";
            default -> "Last 30 days";
        };
    }

    private static LocalDate capSpan(LocalDate start, LocalDate end) {
        long span = ChronoUnit.DAYS.between(start, end);
        return span > MAX_SPAN_DAYS ? end.minusDays(MAX_SPAN_DAYS) : start;
    }

    private static LocalDate parseDateSafe(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) return null;
        try {
            return LocalDate.parse(isoDate);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
