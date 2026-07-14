package com.portfolio.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String key, int tokens, Duration duration) {
        return buckets.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(tokens, Refill.greedy(tokens, duration));
            return Bucket.builder().addLimit(limit).build();
        });
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String ip = getClientIp(request);
        String method = request.getMethod();

        Bucket bucket = null;

        // Auth endpoints: 10 requests per minute per IP
        if ("POST".equals(method) && (
                path.contains("/admin/auth/login") ||
                path.contains("/admin/auth/register") ||
                path.contains("/admin/auth/verify-otp") ||
                path.contains("/admin/auth/resend-otp") ||
                path.contains("/admin/auth/forgot-password") ||
                path.contains("/admin/auth/reset-password"))) {
            bucket = resolveBucket("auth:" + ip, 10, Duration.ofMinutes(1));
        }
        // Contact form: 5 per hour per IP
        else if ("POST".equals(method) && path.contains("/public/contact")) {
            bucket = resolveBucket("contact:" + ip, 5, Duration.ofHours(1));
        }
        // Public portfolio fetch: 60 per minute per IP
        else if ("GET".equals(method) && path.contains("/public/profile/")) {
            bucket = resolveBucket("public:" + ip, 60, Duration.ofMinutes(1));
        }

        if (bucket != null && !bucket.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Please try again later.\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
