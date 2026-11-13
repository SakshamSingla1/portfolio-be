package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ProfileVisibilityEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Covers the PRIVATE-portfolio access gate in isolation. The one thing this must never regress
 * on: assertAccessAllowed has to be a fresh check every call (no caching of its own) since it's
 * the only thing standing between a wrong/missing token and the cached profile-master data --
 * see ProfileAccessServiceImpl's class javadoc for why that separation matters.
 */
class ProfileAccessServiceImplTest {

    private final ProfileDomainResolver profileDomainResolver = mock(ProfileDomainResolver.class);
    private final ProfileDao profileDao = mock(ProfileDao.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);

    private final ProfileAccessServiceImpl service =
            new ProfileAccessServiceImpl(profileDomainResolver, profileDao, passwordEncoder, jwtUtil);

    private static final String HOST = "someone.example.com";
    private static final long PROFILE_ID = 42L;

    private Profile profile(ProfileVisibilityEnum visibility, String accessCodeHash) {
        return Profile.builder().id(PROFILE_ID).visibility(visibility).accessCodeHash(accessCodeHash).build();
    }

    @Test
    void assertAccessAllowed_publicProfile_neverChecksToken() throws Exception {
        when(profileDomainResolver.resolveProfileId(HOST)).thenReturn(PROFILE_ID);
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile(ProfileVisibilityEnum.PUBLIC, null)));

        service.assertAccessAllowed(HOST, null);

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void assertAccessAllowed_privateProfile_missingToken_throwsAccessRequired() throws Exception {
        when(profileDomainResolver.resolveProfileId(HOST)).thenReturn(PROFILE_ID);
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile(ProfileVisibilityEnum.PRIVATE, "hash")));
        when(jwtUtil.isValidProfileAccessToken(null, PROFILE_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.assertAccessAllowed(HOST, null))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.PROFILE_ACCESS_REQUIRED);
    }

    @Test
    void assertAccessAllowed_privateProfile_validToken_passes() throws Exception {
        when(profileDomainResolver.resolveProfileId(HOST)).thenReturn(PROFILE_ID);
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile(ProfileVisibilityEnum.PRIVATE, "hash")));
        when(jwtUtil.isValidProfileAccessToken("good-token", PROFILE_ID)).thenReturn(true);

        service.assertAccessAllowed(HOST, "good-token");
    }

    @Test
    void verifyAccessCode_correctCode_returnsToken() throws Exception {
        when(profileDomainResolver.resolveProfileId(HOST)).thenReturn(PROFILE_ID);
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile(ProfileVisibilityEnum.PRIVATE, "hash")));
        when(passwordEncoder.matches("correct-code", "hash")).thenReturn(true);
        when(jwtUtil.generateProfileAccessToken(PROFILE_ID)).thenReturn("issued-token");

        String token = service.verifyAccessCode(HOST, "correct-code");

        assertThat(token).isEqualTo("issued-token");
    }

    @Test
    void verifyAccessCode_wrongCode_throwsInvalidAccessCode() throws Exception {
        when(profileDomainResolver.resolveProfileId(HOST)).thenReturn(PROFILE_ID);
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile(ProfileVisibilityEnum.PRIVATE, "hash")));
        when(passwordEncoder.matches("wrong-code", "hash")).thenReturn(false);

        assertThatThrownBy(() -> service.verifyAccessCode(HOST, "wrong-code"))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.INVALID_ACCESS_CODE);
    }

    @Test
    void verifyAccessCode_profileNotPrivate_throwsBadRequest() throws Exception {
        when(profileDomainResolver.resolveProfileId(HOST)).thenReturn(PROFILE_ID);
        when(profileDao.findById(PROFILE_ID)).thenReturn(Optional.of(profile(ProfileVisibilityEnum.PUBLIC, null)));

        assertThatThrownBy(() -> service.verifyAccessCode(HOST, "any-code"))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.BAD_REQUEST);
    }
}
