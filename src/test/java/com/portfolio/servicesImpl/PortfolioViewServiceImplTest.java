package com.portfolio.servicesImpl;

import com.portfolio.dao.portfolio_view.PortfolioViewDao;
import com.portfolio.dao.resume.ResumeDownloadDao;
import com.portfolio.dtos.DashboardDTOs.MetricComparisonDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Exercises the Analytics Dashboard's period-over-period percent-change math in isolation
 * (no Spring context, no DB) — this area has no prior test coverage and is easy to get
 * subtly wrong (sign, rounding, zero-baseline handling).
 */
class PortfolioViewServiceImplTest {

    private final PortfolioViewServiceImpl service = new PortfolioViewServiceImpl(
            mock(PortfolioViewDao.class), mock(ResumeDownloadDao.class), mock(RestTemplate.class));

    @ParameterizedTest(name = "current={0}, previous={1} -> percentChange={2}")
    @CsvSource({
            "150, 100, 50.0",
            "50, 100, -50.0",
            "100, 100, 0.0",
            "4, 3, 33.3",
            "0, 0, ''",
            "5, 0, ''",
    })
    void computeComparison_matchesExpectedPercentChange(long current, long previous, String expected) {
        MetricComparisonDTO result = service.computeComparison(current, previous);

        assertThat(result.getCurrent()).isEqualTo(current);
        assertThat(result.getPrevious()).isEqualTo(previous);
        if (expected.isEmpty()) {
            assertThat(result.getPercentChange()).isNull();
        } else {
            assertThat(result.getPercentChange()).isEqualTo(Double.valueOf(expected));
        }
    }
}
