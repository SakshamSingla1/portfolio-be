package com.portfolio.dtos.TestimonialLink;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestimonialLinkPublicResponse {

    private String requesterName;
    private String ownerName;
}
