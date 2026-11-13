package com.portfolio.dtos.ContactUs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ContactThreadResponse {

    private Long contactUsId;
    private String visitorName;
    private String profileName;
    private List<ContactMessageResponse> messages;
}
