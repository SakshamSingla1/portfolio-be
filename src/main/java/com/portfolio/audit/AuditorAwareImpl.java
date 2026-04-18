package com.portfolio.audit;

import com.portfolio.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final JwtUtil jwtUtil;
    
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String currentUserId = (String) request.getAttribute("currentUserId");
                if (currentUserId != null) {
                    log.debug("Current auditor from request: {}", currentUserId);
                    return Optional.of(currentUserId);
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get user ID from request: {}", e.getMessage());
        }
        
        return Optional.of("system");
    }
}
