package com.portfolio.services;

import com.portfolio.dtos.Deployment.DeploymentNotificationRequestDTO;
import com.portfolio.dtos.Deployment.DeploymentNotificationResponseDTO;
import com.portfolio.exceptions.GenericException;

public interface DeploymentNotificationService {

    /**
     * Emails every ACTIVE SUPER_ADMIN profile to let them know the backend deployed
     * successfully. {@code request} may be null — environment/version default to
     * "production"/"unspecified" when not supplied.
     */
    DeploymentNotificationResponseDTO notifySuperAdminsOfDeployment(DeploymentNotificationRequestDTO request) throws GenericException;
}
