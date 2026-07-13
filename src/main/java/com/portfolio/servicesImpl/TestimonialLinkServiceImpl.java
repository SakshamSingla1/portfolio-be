package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dao.testimonial.TestimonialDao;
import com.portfolio.dao.testimonial_request.TestimonialRequestLinkDao;
import com.portfolio.dtos.TestimonialLink.CreateTestimonialLinkRequest;
import com.portfolio.dtos.TestimonialLink.TestimonialLinkPublicResponse;
import com.portfolio.dtos.TestimonialLink.TestimonialLinkResponse;
import com.portfolio.dtos.TestimonialLink.TestimonialSubmitRequest;
import com.portfolio.entities.Profile;
import com.portfolio.entities.Testimonial;
import com.portfolio.entities.TestimonialRequestLink;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.EmailService;
import com.portfolio.services.TestimonialLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestimonialLinkServiceImpl implements TestimonialLinkService {

    private final TestimonialRequestLinkDao testimonialRequestLinkDao;
    private final ProfileDao profileDao;
    private final TestimonialDao testimonialDao;
    private final EmailService emailService;

    @Value("${portfolio.public.base-url:http://localhost:5173}")
    private String publicBaseUrl;

    @Override
    @Transactional
    public TestimonialLinkResponse createLink(Long profileId, CreateTestimonialLinkRequest req) {
        String token = UUID.randomUUID().toString().replace("-", "");
        int expiryDays = req.getExpiryDays() != null ? req.getExpiryDays() : 30;

        TestimonialRequestLink link = TestimonialRequestLink.builder()
                .profileId(profileId)
                .token(token)
                .requesterName(req.getRequesterName())
                .requesterEmail(req.getRequesterEmail())
                .expiresAt(LocalDateTime.now().plusDays(expiryDays))
                .build();

        TestimonialRequestLink saved = testimonialRequestLinkDao.save(link);
        return mapToResponse(saved);
    }

    @Override
    public List<TestimonialLinkResponse> getLinks(Long profileId) {
        return testimonialRequestLinkDao.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Void revokeLink(Long profileId, Long linkId) throws GenericException {
        TestimonialRequestLink link = testimonialRequestLinkDao.findById(linkId)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TESTIMONIAL_REQUEST_NOT_FOUND, "Testimonial request link not found"));

        if (!link.getProfileId().equals(profileId)) {
            throw new GenericException(ExceptionCodeEnum.TESTIMONIAL_REQUEST_NOT_FOUND, "Testimonial request link not found");
        }

        testimonialRequestLinkDao.deleteById(linkId);
        return null;
    }

    @Override
    public TestimonialLinkPublicResponse getPublicLinkDetails(String token) throws GenericException {
        TestimonialRequestLink link = testimonialRequestLinkDao.findByToken(token)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TESTIMONIAL_REQUEST_NOT_FOUND, "Testimonial request link not found"));

        if (LocalDateTime.now().isAfter(link.getExpiresAt())) {
            throw new GenericException(ExceptionCodeEnum.TESTIMONIAL_REQUEST_EXPIRED, "This testimonial request link has expired");
        }

        if (link.getUsedAt() != null) {
            throw new GenericException(ExceptionCodeEnum.TESTIMONIAL_REQUEST_ALREADY_USED, "This testimonial request link has already been used");
        }

        Profile profile = profileDao.findById(link.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        return TestimonialLinkPublicResponse.builder()
                .ownerName(profile.getFullName())
                .requesterName(link.getRequesterName())
                .build();
    }

    @Override
    @Transactional
    public void submitTestimonial(String token, TestimonialSubmitRequest req) throws GenericException {
        TestimonialRequestLink link = testimonialRequestLinkDao.findByToken(token)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.TESTIMONIAL_REQUEST_NOT_FOUND, "Testimonial request link not found"));

        if (LocalDateTime.now().isAfter(link.getExpiresAt())) {
            throw new GenericException(ExceptionCodeEnum.TESTIMONIAL_REQUEST_EXPIRED, "This testimonial request link has expired");
        }

        if (link.getUsedAt() != null) {
            throw new GenericException(ExceptionCodeEnum.TESTIMONIAL_REQUEST_ALREADY_USED, "This testimonial request link has already been used");
        }

        Profile profile = profileDao.findById(link.getProfileId())
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found"));

        Testimonial testimonial = Testimonial.builder()
                .profileId(link.getProfileId())
                .name(req.getName())
                .role(req.getRole())
                .company(req.getCompany())
                .message(req.getMessage())
                .linkedInUrl(req.getLinkedInUrl())
                .status(StatusEnum.PENDING)
                .order(0)
                .build();

        testimonialDao.save(testimonial);

        link.setUsedAt(LocalDateTime.now());
        testimonialRequestLinkDao.save(link);

        try {
            String subject = "New testimonial submitted";
            String html = "<p>Hi " + profile.getFullName() + ",</p>" +
                    "<p><strong>" + req.getName() + "</strong> has submitted a testimonial for your portfolio.</p>" +
                    "<p>Log in to your admin panel to review and approve it.</p>";
            emailService.sendEmail(profile.getEmail(), subject, html);
        } catch (Exception e) {
            log.warn("Failed to send testimonial notification email: {}", e.getMessage());
        }
    }

    private TestimonialLinkResponse mapToResponse(TestimonialRequestLink link) {
        return TestimonialLinkResponse.builder()
                .id(link.getId())
                .profileId(link.getProfileId())
                .requesterName(link.getRequesterName())
                .requesterEmail(link.getRequesterEmail())
                .token(link.getToken())
                .shareUrl(publicBaseUrl + "/testimonial/" + link.getToken())
                .expiresAt(link.getExpiresAt())
                .usedAt(link.getUsedAt())
                .createdAt(link.getCreatedAt())
                .build();
    }
}
