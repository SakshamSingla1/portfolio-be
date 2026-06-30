package com.portfolio.servicesImpl;

import com.portfolio.dao.landing.LandingAudienceCardDao;
import com.portfolio.dao.landing.LandingFaqDao;
import com.portfolio.dao.landing.LandingFeatureDao;
import com.portfolio.dao.landing.LandingHowToUseStepDao;
import com.portfolio.dao.landing.LandingPageConfigDao;
import com.portfolio.dao.landing.LandingTestimonialDao;
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
import com.portfolio.entities.LandingAudienceCard;
import com.portfolio.entities.LandingFaq;
import com.portfolio.entities.LandingFeature;
import com.portfolio.entities.LandingHowToUseStep;
import com.portfolio.entities.LandingPageConfig;
import com.portfolio.entities.LandingTestimonial;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.LandingPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandingPageServiceImpl implements LandingPageService {

    private final LandingPageConfigDao configRepository;
    private final LandingFeatureDao featureRepository;
    private final LandingFaqDao faqRepository;
    private final LandingHowToUseStepDao stepRepository;
    private final LandingAudienceCardDao audienceCardRepository;
    private final LandingTestimonialDao landingTestimonialDao;

    // ── Public ────────────────────────────────────────────────────────────

    @Override
    public LandingPageResponse getPublicLandingPage() {
        LandingPageConfig config = configRepository.findAll().stream().findFirst().orElse(null);
        return LandingPageResponse.builder()
                .config(config != null ? mapToConfigResponse(config) : null)
                .features(featureRepository.findActiveAsDTOs())
                .faqs(faqRepository.findActiveAsDTOs())
                .steps(stepRepository.findByIsActiveTrueOrderBySortOrderAsc()
                        .stream().map(this::mapToStepResponse).toList())
                .audienceCards(audienceCardRepository.findActiveAsDTOs())
                .testimonials(landingTestimonialDao.findActiveAsDTOs())
                .build();
    }

    // ── Config (singleton upsert) ─────────────────────────────────────────

    @Override
    public LandingConfigResponse getConfig() {
        return configRepository.findAll().stream()
                .findFirst()
                .map(this::mapToConfigResponse)
                .orElse(LandingConfigResponse.builder().build());
    }

    @Override
    public LandingConfigResponse updateConfig(LandingConfigRequest request) throws GenericException {
        LandingPageConfig config = configRepository.findAll().stream()
                .findFirst()
                .orElse(new LandingPageConfig());

        config.setHeroEyebrow(request.getHeroEyebrow());
        config.setHeroHeadline1(request.getHeroHeadline1());
        config.setHeroHeadline2(request.getHeroHeadline2());
        config.setHeroDescription(request.getHeroDescription());
        config.setHeroPrimaryCtaText(request.getHeroPrimaryCtaText());
        config.setHeroSecondaryCtaText(request.getHeroSecondaryCtaText());
        config.setHeroTrustBadges(request.getHeroTrustBadges());
        config.setCtaBadgeText(request.getCtaBadgeText());
        config.setCtaHeadline(request.getCtaHeadline());
        config.setCtaDescription(request.getCtaDescription());
        config.setCtaButtonText(request.getCtaButtonText());
        config.setCtaTrustPoints(request.getCtaTrustPoints());

        return mapToConfigResponse(configRepository.save(config));
    }

    // ── Features ──────────────────────────────────────────────────────────

    @Override
    public List<LandingFeatureResponse> getFeatures() {
        return featureRepository.findAll().stream().map(this::mapToFeatureResponse).toList();
    }

    @Override
    public LandingFeatureResponse createFeature(LandingFeatureRequest req) throws GenericException {
        LandingFeature feature = LandingFeature.builder()
                .iconName(req.getIconName())
                .colorKey(req.getColorKey())
                .title(req.getTitle())
                .description(req.getDescription())
                .sortOrder(req.getSortOrder())
                .isActive(req.isActive())
                .build();
        return mapToFeatureResponse(featureRepository.save(feature));
    }

    @Override
    public LandingFeatureResponse updateFeature(Long id, LandingFeatureRequest req) throws GenericException {
        LandingFeature feature = featureRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_FEATURE_NOT_FOUND, "Feature not found"));

        feature.setIconName(req.getIconName());
        feature.setColorKey(req.getColorKey());
        feature.setTitle(req.getTitle());
        feature.setDescription(req.getDescription());
        feature.setSortOrder(req.getSortOrder());
        feature.setActive(req.isActive());

        return mapToFeatureResponse(featureRepository.save(feature));
    }

    @Override
    public void deleteFeature(Long id) throws GenericException {
        LandingFeature feature = featureRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_FEATURE_NOT_FOUND, "Feature not found"));
        featureRepository.delete(feature);
    }

    // ── FAQs ──────────────────────────────────────────────────────────────

    @Override
    public List<LandingFaqResponse> getFaqs() {
        return faqRepository.findAll().stream().map(this::mapToFaqResponse).toList();
    }

    @Override
    public LandingFaqResponse createFaq(LandingFaqRequest req) {
        LandingFaq faq = LandingFaq.builder()
                .question(req.getQuestion())
                .answer(req.getAnswer())
                .sortOrder(req.getSortOrder())
                .isActive(req.isActive())
                .build();
        return mapToFaqResponse(faqRepository.save(faq));
    }

    @Override
    public LandingFaqResponse updateFaq(Long id, LandingFaqRequest req) throws GenericException {
        LandingFaq faq = faqRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_FAQ_NOT_FOUND, "FAQ not found"));

        faq.setQuestion(req.getQuestion());
        faq.setAnswer(req.getAnswer());
        faq.setSortOrder(req.getSortOrder());
        faq.setActive(req.isActive());

        return mapToFaqResponse(faqRepository.save(faq));
    }

    @Override
    public void deleteFaq(Long id) throws GenericException {
        LandingFaq faq = faqRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_FAQ_NOT_FOUND, "FAQ not found"));
        faqRepository.delete(faq);
    }

    // ── How-To-Use Steps ─────────────────────────────────────────────────

    @Override
    public List<LandingHowToUseStepResponse> getSteps() {
        return stepRepository.findAll().stream().map(this::mapToStepResponse).toList();
    }

    @Override
    public LandingHowToUseStepResponse createStep(LandingHowToUseStepRequest req) throws GenericException {
        LandingHowToUseStep step = LandingHowToUseStep.builder()
                .stepNumber(req.getStepNumber())
                .iconName(req.getIconName())
                .colorKey(req.getColorKey())
                .title(req.getTitle())
                .bullets(req.getBullets())
                .sortOrder(req.getSortOrder())
                .isActive(req.isActive())
                .build();
        return mapToStepResponse(stepRepository.save(step));
    }

    @Override
    public LandingHowToUseStepResponse updateStep(Long id, LandingHowToUseStepRequest req) throws GenericException {
        LandingHowToUseStep step = stepRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_STEP_NOT_FOUND, "Step not found"));

        step.setStepNumber(req.getStepNumber());
        step.setIconName(req.getIconName());
        step.setColorKey(req.getColorKey());
        step.setTitle(req.getTitle());
        step.setBullets(req.getBullets());
        step.setSortOrder(req.getSortOrder());
        step.setActive(req.isActive());

        return mapToStepResponse(stepRepository.save(step));
    }

    @Override
    public void deleteStep(Long id) throws GenericException {
        LandingHowToUseStep step = stepRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_STEP_NOT_FOUND, "Step not found"));
        stepRepository.delete(step);
    }

    // ── Audience Cards ───────────────────────────────────────────────────

    @Override
    public List<LandingAudienceCardResponse> getAudienceCards() {
        return audienceCardRepository.findAll().stream().map(this::mapToAudienceCardResponse).toList();
    }

    @Override
    public LandingAudienceCardResponse createAudienceCard(LandingAudienceCardRequest req) throws GenericException {
        LandingAudienceCard card = LandingAudienceCard.builder()
                .iconName(req.getIconName())
                .colorKey(req.getColorKey())
                .title(req.getTitle())
                .description(req.getDescription())
                .sortOrder(req.getSortOrder())
                .isActive(req.isActive())
                .build();
        return mapToAudienceCardResponse(audienceCardRepository.save(card));
    }

    @Override
    public LandingAudienceCardResponse updateAudienceCard(Long id, LandingAudienceCardRequest req) throws GenericException {
        LandingAudienceCard card = audienceCardRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_AUDIENCE_CARD_NOT_FOUND, "Audience card not found"));

        card.setIconName(req.getIconName());
        card.setColorKey(req.getColorKey());
        card.setTitle(req.getTitle());
        card.setDescription(req.getDescription());
        card.setSortOrder(req.getSortOrder());
        card.setActive(req.isActive());

        return mapToAudienceCardResponse(audienceCardRepository.save(card));
    }

    @Override
    public void deleteAudienceCard(Long id) throws GenericException {
        LandingAudienceCard card = audienceCardRepository.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_AUDIENCE_CARD_NOT_FOUND, "Audience card not found"));
        audienceCardRepository.delete(card);
    }

    // ── Testimonials ──────────────────────────────────────────────────────

    @Override
    public List<LandingTestimonialResponse> getTestimonials() {
        return landingTestimonialDao.findAll().stream().map(this::mapToTestimonialResponse).toList();
    }

    @Override
    public LandingTestimonialResponse createTestimonial(LandingTestimonialRequest req) throws GenericException {
        LandingTestimonial testimonial = LandingTestimonial.builder()
                .authorName(req.getAuthorName())
                .authorRole(req.getAuthorRole())
                .authorCompany(req.getAuthorCompany())
                .avatarUrl(req.getAvatarUrl())
                .content(req.getContent())
                .linkedinUrl(req.getLinkedinUrl())
                .sortOrder(req.getSortOrder())
                .isActive(req.isActive())
                .build();
        return mapToTestimonialResponse(landingTestimonialDao.save(testimonial));
    }

    @Override
    public LandingTestimonialResponse updateTestimonial(Long id, LandingTestimonialRequest req) throws GenericException {
        LandingTestimonial testimonial = landingTestimonialDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_TESTIMONIAL_NOT_FOUND, "Testimonial not found"));

        testimonial.setAuthorName(req.getAuthorName());
        testimonial.setAuthorRole(req.getAuthorRole());
        testimonial.setAuthorCompany(req.getAuthorCompany());
        testimonial.setAvatarUrl(req.getAvatarUrl());
        testimonial.setContent(req.getContent());
        testimonial.setLinkedinUrl(req.getLinkedinUrl());
        testimonial.setSortOrder(req.getSortOrder());
        testimonial.setActive(req.isActive());

        return mapToTestimonialResponse(landingTestimonialDao.save(testimonial));
    }

    @Override
    public void deleteTestimonial(Long id) throws GenericException {
        LandingTestimonial testimonial = landingTestimonialDao.findById(id)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.LANDING_TESTIMONIAL_NOT_FOUND, "Testimonial not found"));
        landingTestimonialDao.delete(testimonial);
    }

    // ── Mappers ───────────────────────────────────────────────────────────

    private LandingConfigResponse mapToConfigResponse(LandingPageConfig c) {
        return LandingConfigResponse.builder()
                .id(c.getId())
                .heroEyebrow(c.getHeroEyebrow())
                .heroHeadline1(c.getHeroHeadline1())
                .heroHeadline2(c.getHeroHeadline2())
                .heroDescription(c.getHeroDescription())
                .heroPrimaryCtaText(c.getHeroPrimaryCtaText())
                .heroSecondaryCtaText(c.getHeroSecondaryCtaText())
                .heroTrustBadges(c.getHeroTrustBadges())
                .ctaBadgeText(c.getCtaBadgeText())
                .ctaHeadline(c.getCtaHeadline())
                .ctaDescription(c.getCtaDescription())
                .ctaButtonText(c.getCtaButtonText())
                .ctaTrustPoints(c.getCtaTrustPoints())
                .build();
    }

    private LandingFeatureResponse mapToFeatureResponse(LandingFeature f) {
        return LandingFeatureResponse.builder()
                .id(f.getId())
                .iconName(f.getIconName())
                .colorKey(f.getColorKey())
                .title(f.getTitle())
                .description(f.getDescription())
                .sortOrder(f.getSortOrder())
                .isActive(f.isActive())
                .build();
    }

    private LandingFaqResponse mapToFaqResponse(LandingFaq f) {
        return LandingFaqResponse.builder()
                .id(f.getId())
                .question(f.getQuestion())
                .answer(f.getAnswer())
                .sortOrder(f.getSortOrder())
                .isActive(f.isActive())
                .build();
    }

    private LandingHowToUseStepResponse mapToStepResponse(LandingHowToUseStep s) {
        return LandingHowToUseStepResponse.builder()
                .id(s.getId())
                .stepNumber(s.getStepNumber())
                .iconName(s.getIconName())
                .colorKey(s.getColorKey())
                .title(s.getTitle())
                .bullets(s.getBullets())
                .sortOrder(s.getSortOrder())
                .isActive(s.isActive())
                .build();
    }

    private LandingAudienceCardResponse mapToAudienceCardResponse(LandingAudienceCard c) {
        return LandingAudienceCardResponse.builder()
                .id(c.getId())
                .iconName(c.getIconName())
                .colorKey(c.getColorKey())
                .title(c.getTitle())
                .description(c.getDescription())
                .sortOrder(c.getSortOrder())
                .isActive(c.isActive())
                .build();
    }

    private LandingTestimonialResponse mapToTestimonialResponse(LandingTestimonial t) {
        return LandingTestimonialResponse.builder()
                .id(t.getId())
                .authorName(t.getAuthorName())
                .authorRole(t.getAuthorRole())
                .authorCompany(t.getAuthorCompany())
                .avatarUrl(t.getAvatarUrl())
                .content(t.getContent())
                .linkedinUrl(t.getLinkedinUrl())
                .sortOrder(t.getSortOrder())
                .isActive(t.isActive())
                .build();
    }
}
