package com.portfolio.services;

import com.portfolio.dtos.TestimonialLink.CreateTestimonialLinkRequest;
import com.portfolio.dtos.TestimonialLink.TestimonialLinkPublicResponse;
import com.portfolio.dtos.TestimonialLink.TestimonialLinkResponse;
import com.portfolio.dtos.TestimonialLink.TestimonialSubmitRequest;
import com.portfolio.exceptions.GenericException;

import java.util.List;

public interface TestimonialLinkService {

    TestimonialLinkResponse createLink(Long profileId, CreateTestimonialLinkRequest req);

    List<TestimonialLinkResponse> getLinks(Long profileId);

    Void revokeLink(Long profileId, Long linkId) throws GenericException;

    TestimonialLinkPublicResponse getPublicLinkDetails(String token) throws GenericException;

    void submitTestimonial(String token, TestimonialSubmitRequest req) throws GenericException;
}
