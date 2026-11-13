package com.portfolio.utils;

import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;

// Shared by ProfileMasterServiceImpl (resolving the Referer header into a host) and
// ProfileAccessServiceImpl (the same resolution, for the private-portfolio access gate) --
// both need to turn a raw Referer/registered-URL value into a bare, comparable host.
public final class DomainUtils {

    private DomainUtils() {
    }

    public static String normalizeDomain(String host) throws GenericException {
        if (host == null || host.isBlank()) {
            throw new GenericException(ExceptionCodeEnum.BAD_REQUEST, "Invalid domain");
        }
        return stripToHost(host);
    }

    public static String stripToHost(String value) {
        if (value == null) return "";
        String result = value.trim().toLowerCase().replaceFirst("^https?://", "");
        int slashIndex = result.indexOf('/');
        if (slashIndex >= 0) result = result.substring(0, slashIndex);
        int colonIndex = result.indexOf(':');
        if (colonIndex >= 0) result = result.substring(0, colonIndex);
        if (result.startsWith("www.")) result = result.substring(4);
        return result;
    }
}
