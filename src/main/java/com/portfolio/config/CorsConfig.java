package com.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;
import java.util.Set;

@Configuration
public class CorsConfig {

    private final DynamicCorsOrigins dynamicCorsOrigins;

    public CorsConfig(DynamicCorsOrigins dynamicCorsOrigins) {
        this.dynamicCorsOrigins = dynamicCorsOrigins;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // register a config that resolves dynamically per request
        source.registerCorsConfiguration("/api/**", new CorsConfiguration() {
            @Override
            public List<String> getAllowedOrigins() {
                Set<String> originSet = dynamicCorsOrigins.getOrigins();
                List<String> origins = originSet.stream().toList();

                if (origins.isEmpty()) {
                    origins = List.of(
                            "http://localhost:3000",
                            "http://127.0.0.1:3000",
                            "http://localhost:5173",
                            "http://127.0.0.1:5173",
                            "http://localhost:5174",
                            "http://127.0.0.1:5174",
                            "https://portfolio-fe-66y6.vercel.app"
                    );
                }
                System.out.println("1"+origins);
                return origins;
            }
        });

        return new CorsFilter(source);
    }
}
