package com.portfolio.servicesImpl;

import com.portfolio.dao.file.FileAssetDao;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.role.RoleDao;
import com.portfolio.dao.subscription.ProfileSubscriptionDao;
import com.portfolio.dao.subscription.SubscriptionPlanDao;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.Profile.ProfileSettingsRequest;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ProfileVisibilityEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.FileService;
import com.portfolio.services.NTService;
import com.portfolio.utils.Helper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Covers the one rule that keeps PRIVATE visibility from locking a profile's own owner out:
 * visibility can only move to PRIVATE once an access code already exists. setAccessCode and
 * updateSettings are the only two methods under test here -- everything else ProfileServiceImpl
 * depends on is mocked but never exercised.
 */
class ProfileServiceImplVisibilityTest {

    private final ProfileDao profileDao = mock(ProfileDao.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    private final ProfileServiceImpl service = new ProfileServiceImpl(
            profileDao, mock(FileService.class), mock(FileAssetDao.class), mock(Helper.class),
            mock(RoleDao.class), mock(NTService.class), mock(ProfileSubscriptionDao.class),
            mock(SubscriptionPlanDao.class), passwordEncoder);

    private static final long PROFILE_ID = 7L;

    @Test
    void updateSettings_goingPrivateWithoutAccessCode_isRejected() {
        Profile profile = Profile.builder().id(PROFILE_ID).visibility(ProfileVisibilityEnum.PUBLIC).accessCodeHash(null).build();
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile));

        ProfileSettingsRequest req = new ProfileSettingsRequest();
        req.setVisibility(ProfileVisibilityEnum.PRIVATE);

        assertThatThrownBy(() -> service.updateSettings(PROFILE_ID, req))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.BAD_REQUEST);

        verify(profileDao, never()).save(any());
    }

    @Test
    void updateSettings_goingPrivateWithAccessCodeAlreadySet_succeeds() throws Exception {
        Profile profile = Profile.builder().id(PROFILE_ID).visibility(ProfileVisibilityEnum.PUBLIC).accessCodeHash("existing-hash").build();
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(profileDao.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileSettingsRequest req = new ProfileSettingsRequest();
        req.setVisibility(ProfileVisibilityEnum.PRIVATE);

        ProfileResponse response = service.updateSettings(PROFILE_ID, req);

        assertThat(response.getVisibility()).isEqualTo("PRIVATE");
        verify(profileDao).save(argThat(p -> p.getVisibility() == ProfileVisibilityEnum.PRIVATE));
    }

    @Test
    void setAccessCode_hashesBeforeStoring_neverStoresRawCode() throws Exception {
        Profile profile = Profile.builder().id(PROFILE_ID).visibility(ProfileVisibilityEnum.PUBLIC).accessCodeHash(null).build();
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(passwordEncoder.encode("raw-code-123")).thenReturn("bcrypt-hash");
        when(profileDao.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        ProfileResponse response = service.setAccessCode(PROFILE_ID, "raw-code-123");

        assertThat(response.isHasAccessCode()).isTrue();
        verify(profileDao).save(argThat(p -> "bcrypt-hash".equals(p.getAccessCodeHash())));
    }
}
