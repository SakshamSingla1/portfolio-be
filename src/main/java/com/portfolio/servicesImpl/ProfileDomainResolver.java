package com.portfolio.servicesImpl;

import com.portfolio.dao.social_links.SocialLinksDao;
import com.portfolio.entities.SocialLinks;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.utils.DomainUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

// Kept as its own bean (rather than a private method on ProfileMasterServiceImpl) so both that
// class and ProfileAccessServiceImpl's visitor-facing access gate can share one cached
// host -> profileId resolution instead of each re-deriving it. Caching this mapping is safe even
// for a PRIVATE profile: it's just "which profile owns this domain" (public by construction --
// the owner chose to point this domain here), never the profile's actual portfolio data, so it
// carries none of the cache-poisoning risk that caching the gated data itself would.
@Component
@RequiredArgsConstructor
public class ProfileDomainResolver {

    private final SocialLinksDao socialLinksDao;

    @Cacheable(cacheNames = "profileIdByDomain", key = "#host")
    public Long resolveProfileId(String host) throws GenericException {
        String domain = DomainUtils.normalizeDomain(host);
        SocialLinks socialLink = socialLinksDao.findByPlatform(PlatformEnum.PORTFOLIO).stream()
                .filter(link -> domain.equals(DomainUtils.stripToHost(link.getUrl())))
                .findFirst()
                .orElseThrow(
                        () -> new GenericException(ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND, "Social Link not found"));
        return socialLink.getProfileId();
    }
}
