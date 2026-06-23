package com.portfolio.repositories;

import com.portfolio.entities.SeoMeta;
import com.portfolio.enums.PageKeyEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeoMetaRepository extends JpaRepository<SeoMeta, Long> {
    Optional<SeoMeta> findByProfileIdAndPageKey(Long profileId, PageKeyEnum pageKey);
    List<SeoMeta> findAllByProfileId(Long profileId);
}
