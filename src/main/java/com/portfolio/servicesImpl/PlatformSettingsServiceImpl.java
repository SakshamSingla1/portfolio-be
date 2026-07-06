package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dtos.Platform.PlatformSettingsDTO;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.services.PlatformSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformSettingsServiceImpl implements PlatformSettingsService {

    private static final Long BANNER_RESOURCE_ID = -1L;

    private final FileAssetDao fileAssetDao;

    @Override
    public PlatformSettingsDTO getSettings() {
        return fileAssetDao
                .findByResourceIdAndResourceTypeAndIsPrimaryTrue(BANNER_RESOURCE_ID, ResourceTypeEnum.BANNER)
                .map(asset -> PlatformSettingsDTO.builder()
                        .bannerImageUrl(asset.getPath())
                        .bannerAssetId(String.valueOf(asset.getId()))
                        .build())
                .orElse(PlatformSettingsDTO.builder().build());
    }
}
