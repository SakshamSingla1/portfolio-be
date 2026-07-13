package com.portfolio.servicesImpl;

import com.portfolio.dao.github.GithubIntegrationDao;
import com.portfolio.services.GithubIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubSyncScheduler {

    private final GithubIntegrationDao integrationDao;
    private final GithubIntegrationService githubIntegrationService;

    @Scheduled(cron = "0 0 */6 * * *")
    public void syncAll() {
        integrationDao.findByIsActiveTrue().forEach(integration -> {
            try {
                githubIntegrationService.syncRepos(integration.getProfileId());
                log.info("Synced GitHub repos for profile {}", integration.getProfileId());
            } catch (Exception e) {
                log.warn("GitHub sync failed for profile {}: {}", integration.getProfileId(), e.getMessage());
            }
        });
    }
}
