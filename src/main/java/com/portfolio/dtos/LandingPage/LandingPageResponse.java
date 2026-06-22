package com.portfolio.dtos.LandingPage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandingPageResponse {
    private LandingConfigResponse config;
    private List<LandingFeatureResponse> features;
    private List<LandingFaqResponse> faqs;
    private List<LandingHowToUseStepResponse> steps;
    private List<LandingAudienceCardResponse> audienceCards;
    private List<LandingTestimonialResponse> testimonials;
}
