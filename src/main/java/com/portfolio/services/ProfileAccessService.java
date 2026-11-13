package com.portfolio.services;

import com.portfolio.exceptions.GenericException;

/**
 * Gates the PRIVATE-visibility portfolio feature: a domain resolves to a profile the same way
 * ProfileMasterServiceImpl's public data fetch does, but the actual data is never cached or
 * returned by anything in here -- see ProfileAccessServiceImpl for why the gate has to stay
 * strictly separate from the cached profile-master data fetch.
 */
public interface ProfileAccessService {

    /** Throws GenericException(PROFILE_ACCESS_REQUIRED) if the profile that owns {@code host}
     * is PRIVATE and {@code accessToken} isn't a valid token for it. No-op for a PUBLIC profile. */
    void assertAccessAllowed(String host, String accessToken) throws GenericException;

    /** Verifies {@code rawAccessCode} against the profile that owns {@code host} and returns a
     * signed access token to replay on subsequent requests. Throws GenericException if the
     * profile isn't PRIVATE/password-protected, or the code is wrong. */
    String verifyAccessCode(String host, String rawAccessCode) throws GenericException;
}
