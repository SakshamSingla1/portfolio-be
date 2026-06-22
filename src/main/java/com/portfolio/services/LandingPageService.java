package com.portfolio.services;

import com.portfolio.dtos.Landing.LandingAudienceCardRequest;
import com.portfolio.dtos.Landing.LandingConfigRequest;
import com.portfolio.dtos.Landing.LandingFaqRequest;
import com.portfolio.dtos.Landing.LandingFeatureRequest;
import com.portfolio.dtos.Landing.LandingHowToUseStepRequest;
import com.portfolio.dtos.Landing.LandingTestimonialRequest;
import com.portfolio.dtos.LandingPage.LandingAudienceCardResponse;
import com.portfolio.dtos.LandingPage.LandingConfigResponse;
import com.portfolio.dtos.LandingPage.LandingFaqResponse;
import com.portfolio.dtos.LandingPage.LandingFeatureResponse;
import com.portfolio.dtos.LandingPage.LandingHowToUseStepResponse;
import com.portfolio.dtos.LandingPage.LandingPageResponse;
import com.portfolio.dtos.LandingPage.LandingTestimonialResponse;
import com.portfolio.exceptions.GenericException;

import java.util.List;

public interface LandingPageService {

    // ── Public ────────────────────────────────────────────────────────────
    LandingPageResponse getPublicLandingPage();

    // ── Config (singleton upsert) ─────────────────────────────────────────
    LandingConfigResponse getConfig();
    LandingConfigResponse updateConfig(LandingConfigRequest request) throws GenericException;

    // ── Features ──────────────────────────────────────────────────────────
    List<LandingFeatureResponse> getFeatures();
    LandingFeatureResponse createFeature(LandingFeatureRequest req) throws GenericException;
    LandingFeatureResponse updateFeature(Long id, LandingFeatureRequest req) throws GenericException;
    void deleteFeature(Long id) throws GenericException;

    // ── FAQs ──────────────────────────────────────────────────────────────
    List<LandingFaqResponse> getFaqs();
    LandingFaqResponse createFaq(LandingFaqRequest req);
    LandingFaqResponse updateFaq(Long id, LandingFaqRequest req) throws GenericException;
    void deleteFaq(Long id) throws GenericException;

    // ── How-To-Use Steps ─────────────────────────────────────────────────
    List<LandingHowToUseStepResponse> getSteps();
    LandingHowToUseStepResponse createStep(LandingHowToUseStepRequest req) throws GenericException;
    LandingHowToUseStepResponse updateStep(Long id, LandingHowToUseStepRequest req) throws GenericException;
    void deleteStep(Long id) throws GenericException;

    // ── Audience Cards ───────────────────────────────────────────────────
    List<LandingAudienceCardResponse> getAudienceCards();
    LandingAudienceCardResponse createAudienceCard(LandingAudienceCardRequest req) throws GenericException;
    LandingAudienceCardResponse updateAudienceCard(Long id, LandingAudienceCardRequest req) throws GenericException;
    void deleteAudienceCard(Long id) throws GenericException;

    // ── Testimonials ──────────────────────────────────────────────────────
    List<LandingTestimonialResponse> getTestimonials();
    LandingTestimonialResponse createTestimonial(LandingTestimonialRequest req) throws GenericException;
    LandingTestimonialResponse updateTestimonial(Long id, LandingTestimonialRequest req) throws GenericException;
    void deleteTestimonial(Long id) throws GenericException;
}
