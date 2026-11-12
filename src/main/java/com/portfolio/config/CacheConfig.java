package com.portfolio.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The public profile-master response aggregates ~15 independent DB lookups per request
 * (see ProfileMasterServiceImpl). That content changes rarely, so a short TTL cache
 * eliminates the whole fan-out on repeat hits without needing write-path eviction wired
 * through every child service.
 *
 * Different cache names get different Caffeine specs (CaffeineCacheManager only supports one
 * shared spec for every cache it manages, so we register individual CaffeineCache beans on a
 * SimpleCacheManager instead):
 *  - Low-cardinality, rarely-changing lookups (color theme, permissions, nav links, landing
 *    page, active FAQs) get a long 30-minute TTL and a modest size cap - there are only ever a
 *    handful of distinct entries for these.
 *  - The profile-master family (keyed per host / per profileId, from ProfileMasterServiceImpl)
 *    is higher-cardinality and more dynamic, so it keeps a short TTL and its own size cap so it
 *    can't crowd out the low-cardinality caches' eviction headroom.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final List<String> LONG_LIVED_CACHES = List.of(
            "colorThemeDefault", "colorThemeById", "permissionsAll",
            "navLinksGrouped", "navLinksAll", "landingPagePublic", "activeFaqs");

    private static final List<String> SHORT_LIVED_CACHES = List.of(
            "profileMasterByHost", "profileMasterByProfileId", "profileMasterResumeExport",
            "activeResumeAsset");

    // The generated portfolio-export PDF (PortfolioExportServiceImpl#exportPdf) is by far the most
    // expensive thing this app computes per-request -- full HTML render + openhtmltopdf font
    // embedding + a Cloudinary upload -- so it gets a longer TTL than the plain DB-lookup caches
    // above: repeat downloads of the same profile within this window are served the already-uploaded
    // URL instead of re-paying for a render, which is what makes it safe to also serialize renders
    // (see PortfolioExportServiceImpl's EXPORT_SEMAPHORE) without every retry just queuing up again.
    private static final List<String> PDF_EXPORT_CACHES = List.of("portfolioExportPdfUrl");

    @Bean
    public CacheManager cacheManager() {
        List<Cache> caches = new ArrayList<>();
        for (String name : LONG_LIVED_CACHES) {
            caches.add(buildCache(name, 30, TimeUnit.MINUTES, 200));
        }
        for (String name : SHORT_LIVED_CACHES) {
            caches.add(buildCache(name, 60, TimeUnit.SECONDS, 500));
        }
        for (String name : PDF_EXPORT_CACHES) {
            caches.add(buildCache(name, 5, TimeUnit.MINUTES, 200));
        }

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private CaffeineCache buildCache(String name, long ttl, TimeUnit unit, int maximumSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(ttl, unit)
                .maximumSize(maximumSize)
                .build());
    }
}
