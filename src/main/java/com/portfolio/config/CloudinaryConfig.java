package com.portfolio.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true,
                // Cloudinary's Configuration defaults "timeout" to 0, which the underlying Apache
                // HttpClient treats as NO connect/socket timeout at all -- a hung or slow Cloudinary
                // connection would then block the calling thread forever. That thread is often
                // holding something else's resource while it waits: PortfolioExportServiceImpl's
                // single-permit export semaphore wraps its upload call, so an unbounded Cloudinary
                // hang there would permanently wedge the whole resume-export feature (every future
                // export request would get stuck as SERVICE_BUSY since the permit never releases).
                "timeout", 30
        ));
    }
}
