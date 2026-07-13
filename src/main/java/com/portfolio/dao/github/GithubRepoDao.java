package com.portfolio.dao.github;

import com.portfolio.entities.GithubRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GithubRepoDao extends JpaRepository<GithubRepo, Long> {
    List<GithubRepo> findByIntegrationIdOrderBySortOrderAscStarsDesc(Long integrationId);
    List<GithubRepo> findByIntegrationIdAndIsVisibleTrueOrderBySortOrderAscStarsDesc(Long integrationId);
    Optional<GithubRepo> findByIntegrationIdAndGithubId(Long integrationId, Long githubId);
    void deleteByIntegrationId(Long integrationId);
}
