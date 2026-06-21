package com.portfolio.servicesImpl;

import com.portfolio.dtos.Platform.PlatformSettingsDTO;
import com.portfolio.enums.ResourceTypeEnum;
import com.portfolio.repositories.FileAssetRepository;
import com.portfolio.services.PlatformSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatformSettingsServiceImpl implements PlatformSettingsService {

    private static final String BANNER_RESOURCE_ID = "singleton";

    private final FileAssetRepository fileAssetRepository;

    @Override
    public PlatformSettingsDTO getSettings() {
        return fileAssetRepository
                .findByResourceIdAndResourceTypeAndIsPrimaryTrue(BANNER_RESOURCE_ID, ResourceTypeEnum.BANNER)
                .map(asset -> PlatformSettingsDTO.builder()
                        .bannerImageUrl(asset.getPath())
                        .bannerAssetId(asset.getId())
                        .build())
                .orElse(PlatformSettingsDTO.builder().build());
    }
}
