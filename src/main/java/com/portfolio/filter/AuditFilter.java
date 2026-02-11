package com.portfolio.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            preProcessRequest(request, response, filterChain);
            filterChain.doFilter(request, response);

        } finally {
            postProcessRequest(request, response, filterChain);
        }
    }

    private void preProcessRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String existingTraceId = (String) request.getAttribute("traceId");
        if (existingTraceId == null || existingTraceId.isBlank()) {
            request.setAttribute("traceId", UUID.randomUUID().toString());
        }
    }

    private void postProcessRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // try {
        // request.setAttribute("traceId", UUID.randomUUID().toString());
        // filterChain.doFilter(request, response);
        // } catch (IOException | ServletException e) {
        // logger.error("Error processing request: " + e.getMessage());
        // }
        // return;
    }
}
