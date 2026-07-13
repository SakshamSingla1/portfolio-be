package com.portfolio.dao.github;

import com.portfolio.entities.GithubIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GithubIntegrationDao extends JpaRepository<GithubIntegration, Long> {
    Optional<GithubIntegration> findByProfileId(Long profileId);
    boolean existsByProfileId(Long profileId);
    void deleteByProfileId(Long profileId);
    List<GithubIntegration> findByIsActiveTrue();
}
