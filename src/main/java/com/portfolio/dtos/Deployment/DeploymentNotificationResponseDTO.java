package com.portfolio.dtos.Deployment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentNotificationResponseDTO {
    private int notifiedCount;
    private List<String> notifiedEmails;
    private String environment;
    private String version;
    private String deployedAt;
}
