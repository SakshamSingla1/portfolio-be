package com.portfolio.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * The public profile-master response aggregates ~15 independent DB lookups per request
 * (see ProfileMasterServiceImpl). That content changes rarely, so a short TTL cache
 * eliminates the whole fan-out on repeat hits without needing write-path eviction wired
 * through every child service.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "profileMasterByHost", "profileMasterByProfileId", "profileMasterResumeExport",
                "navLinksGrouped", "navLinksAll", "permissionsAll",
                "colorThemeDefault", "colorThemeById", "landingPagePublic");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(500));
        return cacheManager;
    }
}
