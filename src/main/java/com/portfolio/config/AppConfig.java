package com.portfolio.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    // @Primary so every existing unqualified RestTemplate injection (geocoding, etc.) keeps
    // resolving to this one unchanged now that a second RestTemplate bean exists below.
    @Bean
    @Primary
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(8))
                .build();
    }

    // A real LLM completion routinely takes longer than the 8s read timeout tuned above for
    // fast external APIs (geocoding, etc.) -- a separate, longer-timeout template for
    // AiContentServiceImpl avoids that call failing under completely normal latency.
    @Bean
    @AiRestTemplate
    public RestTemplate aiRestTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    public @interface AiRestTemplate {
    }

    /**
     * Used to fan out independent per-profile lookups (experiences, educations, skills, etc.)
     * concurrently instead of sequentially — sized to stay well within Hikari's max-pool-size
     * (10) so parallel fetches can't exhaust the connection pool.
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService profileAggregationExecutor() {
        return Executors.newFixedThreadPool(8);
    }
}
