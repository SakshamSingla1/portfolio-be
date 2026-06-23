package com.portfolio.controllers;

import com.portfolio.entities.SeoMeta;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.PageKeyEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.repositories.SeoMetaRepository;
import com.portfolio.repositories.SocialLinkRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SeoEndpointsController {

    private final SocialLinkRepository socialLinkRepository;
    private final SeoMetaRepository seoMetaRepository;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap(HttpServletRequest request) {
        try {
            String host = resolveHost(request);
            Optional<SocialLinks> linkOpt = socialLinkRepository
                    .findByPlatformAndUrlContainingHost(PlatformEnum.PORTFOLIO, host);

            if (linkOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Long profileId = linkOpt.get().getProfileId();
            String siteUrl = resolveSiteUrl(profileId, linkOpt.get().getUrl());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(buildSitemapXml(siteUrl));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> robots(HttpServletRequest request) {
        try {
            String host = resolveHost(request);
            Optional<SocialLinks> linkOpt = socialLinkRepository
                    .findByPlatformAndUrlContainingHost(PlatformEnum.PORTFOLIO, host);

            if (linkOpt.isEmpty()) {
                return ResponseEntity.ok("User-agent: *\nAllow: /\n");
            }

            Long profileId = linkOpt.get().getProfileId();
            SeoMeta seoMeta = seoMetaRepository
                    .findByProfileIdAndPageKey(profileId, PageKeyEnum.HOME)
                    .orElse(null);

            boolean indexable = seoMeta == null || !Boolean.FALSE.equals(seoMeta.getIndexable());
            String siteUrl = resolveSiteUrl(profileId, linkOpt.get().getUrl());

            StringBuilder sb = new StringBuilder("User-agent: *\n");
            sb.append(indexable ? "Allow: /\n" : "Disallow: /\n");
            if (indexable) {
                sb.append("\nSitemap: ").append(siteUrl).append("/sitemap.xml\n");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(sb.toString());
        } catch (Exception e) {
            return ResponseEntity.ok("User-agent: *\nAllow: /\n");
        }
    }

    private String resolveHost(HttpServletRequest request) {
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }
        if (host.contains(":")) host = host.split(":")[0];
        return host.toLowerCase().replace("www.", "").trim();
    }

    private String resolveSiteUrl(Long profileId, String fallbackUrl) {
        return seoMetaRepository
                .findByProfileIdAndPageKey(profileId, PageKeyEnum.HOME)
                .map(SeoMeta::getCanonicalUrl)
                .filter(url -> url != null && !url.isBlank())
                .map(url -> url.endsWith("/") ? url.substring(0, url.length() - 1) : url)
                .orElseGet(() -> {
                    String url = fallbackUrl == null ? "" : fallbackUrl.trim();
                    if (!url.startsWith("http")) url = "https://" + url;
                    if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
                    return url;
                });
    }

    private String buildSitemapXml(String siteUrl) {
        String escaped = siteUrl.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n"
                + "  <url>\n"
                + "    <loc>" + escaped + "</loc>\n"
                + "    <changefreq>weekly</changefreq>\n"
                + "    <priority>1.0</priority>\n"
                + "  </url>\n"
                + "</urlset>";
    }
}
