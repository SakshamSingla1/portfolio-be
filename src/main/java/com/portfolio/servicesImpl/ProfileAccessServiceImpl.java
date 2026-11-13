package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.ProfileVisibilityEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.security.JwtUtil;
import com.portfolio.services.ProfileAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Deliberately NOT @Cacheable and deliberately separate from ProfileMasterServiceImpl's cached
 * getProfileMasterData(host): that method's result is cached per-host and a cache hit bypasses
 * the method body (and any gate inside it) entirely on every subsequent call. If the PRIVATE
 * check lived inside that cached method, the first visitor who ever supplied a valid access code
 * would cause the real portfolio data to be cached under that host key -- and every visitor after
 * them, valid code or not, would be served it straight from cache with no gate ever re-evaluated.
 * Keeping the gate here, as an always-fresh check PublicController runs before calling the cached
 * method at all, means a cache hit can only ever return data the gate already approved for this
 * specific request.
 */
@Service
@RequiredArgsConstructor
public class ProfileAccessServiceImpl implements ProfileAccessService {

    private final ProfileDomainResolver profileDomainResolver;
    private final ProfileDao profileDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void assertAccessAllowed(String host, String accessToken) throws GenericException {
        Long profileId = profileDomainResolver.resolveProfileId(host);
        Profile profile = profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        if (profile.getVisibility() != ProfileVisibilityEnum.PRIVATE) {
            return;
        }
        if (!jwtUtil.isValidProfileAccessToken(accessToken, profileId)) {
            throw new GenericException(ExceptionCodeEnum.PROFILE_ACCESS_REQUIRED,
                    "This portfolio is private -- enter the access code to view it");
        }
    }

    @Override
    public String verifyAccessCode(String host, String rawAccessCode) throws GenericException {
        Long profileId = profileDomainResolver.resolveProfileId(host);
        Profile profile = profileDao.findById(profileId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        if (profile.getVisibility() != ProfileVisibilityEnum.PRIVATE || profile.getAccessCodeHash() == null) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "This portfolio is not password-protected");
        }
        if (!passwordEncoder.matches(rawAccessCode, profile.getAccessCodeHash())) {
            throw new GenericException(ExceptionCodeEnum.INVALID_ACCESS_CODE, "Incorrect access code");
        }
        return jwtUtil.generateProfileAccessToken(profileId);
    }
}
