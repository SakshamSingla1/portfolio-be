package com.portfolio.utils;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure date-bucket math for the Analytics Dashboard's range resolution — no Spring
 * context needed. Avoids asserting against externally-computed "now" timestamps (flaky
 * around midnight/test-run boundaries) in favor of structural invariants instead.
 */
class AnalyticsRangeResolverTest {

    @Test
    void sevenDayRange_spansSixDaysInclusive() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve("7d", null, null);

        assertThat(range.key()).isEqualTo("7d");
        assertThat(ChronoUnit.DAYS.between(range.startDate(), range.endDate())).isEqualTo(6);
        assertThat(range.label()).isEqualTo("Last 7 days");
    }

    @Test
    void nullRangeKey_defaultsTo30d() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve(null, null, null);
        assertThat(range.key()).isEqualTo("30d");
    }

    @Test
    void unrecognisedRangeKey_defaultsTo30d() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve("bogus", null, null);
        assertThat(range.key()).isEqualTo("30d");
    }

    @Test
    void customRange_usesParsedDates() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve("custom", "2024-01-01", "2024-01-10");

        assertThat(range.key()).isEqualTo("custom");
        assertThat(range.startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(range.endDate()).isEqualTo(LocalDate.of(2024, 1, 10));
        assertThat(range.label()).isEqualTo("Custom");
    }

    @Test
    void customRange_withUnparsableDates_fallsBackTo30d() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve("custom", "not-a-date", "2024-01-10");
        assertThat(range.key()).isEqualTo("30d");
    }

    @Test
    void customRange_withStartAfterEnd_fallsBackTo30d() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve("custom", "2024-01-10", "2024-01-01");
        assertThat(range.key()).isEqualTo("30d");
    }

    @Test
    void customRange_longerThanMaxSpan_isCapped() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve("custom", "2020-01-01", "2024-01-01");
        long spanDays = ChronoUnit.DAYS.between(range.startDate(), range.endDate());
        assertThat(spanDays).isLessThanOrEqualTo(366);
    }

    @Test
    void explicitRangeKey_winsOverStrayDateParams() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve("7d", "2024-01-01", "2024-01-10");
        assertThat(range.key()).isEqualTo("7d");
        assertThat(ChronoUnit.DAYS.between(range.startDate(), range.endDate())).isEqualTo(6);
    }

    @Test
    void previousPeriod_isEqualLengthAndImmediatelyPrecedesCurrent() {
        AnalyticsRangeResolver.AnalyticsRange range = AnalyticsRangeResolver.resolve("30d", null, null);

        assertThat(range.prevEnd()).isEqualTo(range.start());
        Duration currentDuration = Duration.between(range.start(), range.end());
        Duration previousDuration = Duration.between(range.prevStart(), range.prevEnd());
        assertThat(previousDuration).isEqualTo(currentDuration);
    }
}
