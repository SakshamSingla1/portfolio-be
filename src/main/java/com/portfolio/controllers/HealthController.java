package com.portfolio.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @Operation(summary = "Health check", description = "Returns 'OK' to confirm the server is running and responsive.")
    @GetMapping("/api/v1/health")
    public String health() {
        return "OK";
    }
}
