package com.portfolio.config;

import com.portfolio.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig {

    @Autowired
    ProfileRepository profileRepository;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                List<String> allowedOrigins = profileRepository.findAll()
                                .stream()
                                .map(profile -> profile.getWebsiteUrl())
                                .filter(url -> url != null && !url.isBlank())
                                .toList();
                if(allowedOrigins.isEmpty()) {
                    allowedOrigins = List.of(
                            "http://localhost:3000",
                            "http://127.0.0.1:3000",
                            "http://localhost:5173",
                            "http://127.0.0.1:5173",
                            "http://localhost:5174",
                            "http://127.0.0.1:5174",
                            "https://portfolio-fe-66y6.vercel.app");
                }
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigins.toArray(new String[0])) // pulled from env/properties
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
                System.out.println(allowedOrigins);
            }
        };
    }
}
