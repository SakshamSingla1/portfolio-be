package com.portfolio.dtos.Deployment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Optional context about the deploy, passed by whatever calls the notify-deployment endpoint
 * (a CI/CD pipeline step, or a SUPER_ADMIN triggering it manually). Every field is optional —
 * {@link com.portfolio.servicesImpl.DeploymentNotificationServiceImpl} fills in sensible
 * defaults for anything left blank.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentNotificationRequestDTO {
    private String environment;
    private String version;
}
