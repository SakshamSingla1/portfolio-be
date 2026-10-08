package com.portfolio.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Small helpers for working with Cloudinary delivery URLs directly (shared by any feature that
 *  redirects the client straight to Cloudinary instead of proxying bytes through this server --
 *  resume download, portfolio PDF export, etc.). */
public final class CloudinaryUrlUtils {

    private CloudinaryUrlUtils() {
    }

    /** Cloudinary serves raw/upload URLs with a forced attachment Content-Disposition (and a
     *  chosen filename) when an "fl_attachment:<filename>" flag is inserted right after "/upload/". */
    public static String withAttachmentFlag(String secureUrl, String fileName) {
        String marker = "/upload/";
        int idx = secureUrl.indexOf(marker);
        if (idx < 0) return secureUrl;
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        int afterMarker = idx + marker.length();
        return secureUrl.substring(0, afterMarker) + "fl_attachment:" + encodedName + "/" + secureUrl.substring(afterMarker);
    }
}
