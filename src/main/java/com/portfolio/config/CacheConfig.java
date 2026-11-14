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
            "activeResumeAsset", "profileIdByDomain");

    @Bean
    public CacheManager cacheManager() {
        List<Cache> caches = new ArrayList<>();
        for (String name : LONG_LIVED_CACHES) {
            caches.add(buildCache(name, 30, TimeUnit.MINUTES, 200));
        }
        for (String name : SHORT_LIVED_CACHES) {
            caches.add(buildCache(name, 60, TimeUnit.SECONDS, 500));
        }
        caches.add(buildPdfBytesCache());

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    // The generated portfolio-export PDF (PortfolioExportServiceImpl#exportPdf) is by far the most
    // expensive thing this app computes per-request -- full HTML render + openhtmltopdf font
    // embedding -- so repeat downloads of the same profile within this window are served the
    // already-rendered bytes straight out of memory instead of paying for another render (which is
    // also what makes it safe to serialize renders -- see PortfolioExportServiceImpl's
    // EXPORT_SEMAPHORE -- without every retry just queuing up behind the one in flight). Caches the
    // PDF bytes themselves (not a Cloudinary URL): nothing else in the app reads that URL, so
    // skipping the upload and streaming the bytes straight back to the client removes a full
    // external network round-trip from every cold request.
    //
    // Sized by total bytes, not entry count, unlike buildCache's other caches: a resume PDF is
    // commonly 100-300KB, and an entry-count cap on a memory-constrained (512MB/320MB-heap)
    // instance is the wrong knob for something whose size varies that much per entry.
    private CaffeineCache buildPdfBytesCache() {
        return new CaffeineCache("portfolioExportPdfBytes", Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumWeight(20L * 1024 * 1024) // 20MB total across every cached resume
                .weigher((Object key, Object value) -> value instanceof byte[] bytes ? bytes.length : 1)
                .build());
    }

    private CaffeineCache buildCache(String name, long ttl, TimeUnit unit, int maximumSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(ttl, unit)
                .maximumSize(maximumSize)
                .build());
    }
}
