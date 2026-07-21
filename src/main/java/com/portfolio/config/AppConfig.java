package com.portfolio.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(8))
                .build();
    }

    /**
     * Used to fan out independent per-profile lookups (experiences, educations, skills, etc.)
     * concurrently instead of sequentially — sized to stay well within Hikari's max-pool-size
     * (20) so parallel fetches can't exhaust the connection pool.
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService profileAggregationExecutor() {
        return Executors.newFixedThreadPool(12);
    }
}
