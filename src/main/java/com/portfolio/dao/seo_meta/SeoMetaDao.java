package com.portfolio.dao.seo_meta;

import com.portfolio.entities.SeoMeta;
import com.portfolio.enums.PageKeyEnum;
import com.portfolio.repositories.SeoMetaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class SeoMetaDao {

    private final SeoMetaRepository seoMetaRepository;

    public SeoMetaDao(SeoMetaRepository seoMetaRepository) {
        this.seoMetaRepository = seoMetaRepository;
    }

    public SeoMeta save(SeoMeta seoMeta) {
        return seoMetaRepository.save(seoMeta);
    }

    public Optional<SeoMeta> findById(Long id) {
        return seoMetaRepository.findById(id);
    }

    public void deleteById(Long id) {
        seoMetaRepository.deleteById(id);
    }

    public Optional<SeoMeta> findByProfileIdAndPageKey(Long profileId, PageKeyEnum pageKey) {
        return seoMetaRepository.findByProfileIdAndPageKey(profileId, pageKey);
    }

    public List<SeoMeta> findAllByProfileId(Long profileId) {
        return seoMetaRepository.findAllByProfileId(profileId);
    }
}
