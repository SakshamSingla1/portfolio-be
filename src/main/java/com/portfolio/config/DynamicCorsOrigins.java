package com.portfolio.config;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class DynamicCorsOrigins {

    private final Set<String> origins = Collections.synchronizedSet(new HashSet<>());

    public void addOrigin(String origin) {
        if (origin != null && !origin.isBlank()) {
            origins.add(origin);
        }
    }

    public Set<String> getOrigins() {
        return Collections.unmodifiableSet(origins);
    }
}
