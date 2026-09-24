package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.role.RoleDao;
import com.portfolio.dtos.Deployment.DeploymentNotificationRequestDTO;
import com.portfolio.dtos.Deployment.DeploymentNotificationResponseDTO;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Role;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.DeploymentNotificationService;
import com.portfolio.services.NTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentNotificationServiceImpl implements DeploymentNotificationService {

    private static final DateTimeFormatter DEPLOYED_AT_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");

    private final ProfileDao profileDao;
    private final RoleDao roleDao;
    private final NTService ntService;

    @Override
    public DeploymentNotificationResponseDTO notifySuperAdminsOfDeployment(DeploymentNotificationRequestDTO request)
            throws GenericException {
        Role superAdminRole = roleDao.findByName("SUPER_ADMIN")
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.ROLE_NOT_FOUND, "SUPER_ADMIN role not found"));

        List<Profile> superAdmins = profileDao.findAllByRoleIdAndStatus(superAdminRole.getId(), StatusEnum.ACTIVE);

        String environment = notBlank(request == null ? null : request.getEnvironment()) ? request.getEnvironment() : "production";
        String version = notBlank(request == null ? null : request.getVersion()) ? request.getVersion() : "unspecified";
        String deployedAt = LocalDateTime.now().format(DEPLOYED_AT_FORMAT);

        // One send per admin, each independently caught so one failure (or one missing
        // mailbox) never blocks the others — same pattern used by WeeklyDigestScheduler
        // and SocialLinkServiceImpl's custom-domain admin broadcast.
        List<String> notifiedEmails = new ArrayList<>();
        for (Profile admin : superAdmins) {
            try {
                ntService.sendNotification(
                        "BACKEND-DEPLOYED",
                        Map.of(
                                "adminName", admin.getFullName(),
                                "environment", environment,
                                "version", version,
                                "deployedAt", deployedAt
                        ),
                        admin.getEmail()
                );
                notifiedEmails.add(admin.getEmail());
            } catch (Exception e) {
                log.warn("Failed to notify super admin {} of deployment: {}", admin.getId(), e.getMessage());
            }
        }

        log.info("Backend deployment notification queued for {} of {} SUPER_ADMIN profiles (env={}, version={})",
                notifiedEmails.size(), superAdmins.size(), environment, version);

        return DeploymentNotificationResponseDTO.builder()
                .notifiedCount(notifiedEmails.size())
                .notifiedEmails(notifiedEmails)
                .environment(environment)
                .version(version)
                .deployedAt(deployedAt)
                .build();
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
