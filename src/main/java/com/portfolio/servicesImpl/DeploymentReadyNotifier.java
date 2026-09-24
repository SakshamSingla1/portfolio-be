package com.portfolio.servicesImpl;

import com.portfolio.dtos.Deployment.DeploymentNotificationRequestDTO;
import com.portfolio.services.DeploymentNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Fires the "backend deployed successfully" SUPER_ADMIN email automatically once the app has
 * fully started, so a deploy no longer needs an explicit call to
 * POST /api/v1/system/notify-deployment.
 * <p>
 * Only active under the "prod" profile ({@code spring.profiles.active} defaults to "dev" — see
 * application.properties), so every local {@code mvn spring-boot:run} during development does
 * NOT trigger this. In production, the Dockerfile's entrypoint just runs the jar directly, so a
 * fresh container/process start corresponds exactly to a fresh deploy — one
 * {@link ApplicationReadyEvent} per release.
 * <p>
 * Known limitation: if the production instance crash-loops (repeatedly restarts without a new
 * deploy actually happening), this fires again on every restart. Not guarded against here since
 * that would need a persistent "last sent" marker surviving process restarts; ask for that as a
 * follow-up if it becomes a real problem.
 */
@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class DeploymentReadyNotifier {

    private final DeploymentNotificationService deploymentNotificationService;

    @Value("${app.version:unspecified}")
    private String appVersion;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            deploymentNotificationService.notifySuperAdminsOfDeployment(
                    DeploymentNotificationRequestDTO.builder()
                            .environment("production")
                            .version(appVersion)
                            .build()
            );
        } catch (Exception e) {
            // Never let a notification failure affect the already-started application.
            log.warn("Failed to send automatic backend-deployed notification: {}", e.getMessage());
        }
    }
}
