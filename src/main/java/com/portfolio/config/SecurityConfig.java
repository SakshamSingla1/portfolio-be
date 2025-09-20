package com.portfolio.config;

import com.portfolio.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // disable csrf for APIs
                .csrf(AbstractHttpConfigurer::disable)
                // enable CORS (configured separately)
                .cors(Customizer.withDefaults())
                // authorize requests
                .authorizeHttpRequests(auth -> auth
                        // Swagger & API docs - public
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-ui/index.html"
                        ).permitAll()

                        // Public GET APIs
                        .requestMatchers(HttpMethod.GET,
                                "api/v1/profile-master/**",
                                "/api/v1/profile/**",
                                "/api/v1/project/**",
                                "/api/v1/skill/**",
                                "/api/v1/education/**",
                                "/api/v1/experience/**",
                                "/api/v1/logo/**"
                        ).permitAll()

                        // Contact form - open
                        .requestMatchers("/api/v1/contact-us/**").permitAll()

                        // ---------------- Admin APIs ----------------
                        // Public admin endpoints (no auth required)
                        .requestMatchers(
                                "/api/v1/admin/register",
                                "/api/v1/admin/verify-otp",
                                "/api/v1/admin/login",
                                "/api/v1/admin/forgot-password",
                                "/api/v1/admin/reset-password",
                                "/api/v1/admin/send-otp"
                        ).permitAll()

                        // Protected admin endpoints (auth required with ROLE_ADMIN)
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // All write operations (POST/PUT/DELETE) need authentication
                        .requestMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )
                // stateless session for JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // custom unauthorized response
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))
                // add JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
        };
    }
}
