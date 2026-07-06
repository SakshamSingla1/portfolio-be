package com.portfolio.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dtos.ContactUs.ContactUsRequest;
import com.portfolio.exceptions.GlobalExceptionHandler;
import com.portfolio.services.ContactUsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContactUsService contactUsService;

    @InjectMocks
    private PublicController publicController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createContactUs_InvalidPayload_Returns400BadRequest() throws Exception {
        // Arrange
        ContactUsRequest request = new ContactUsRequest();
        request.setName(""); // Invalid: blank
        request.setEmail("invalid-email"); // Invalid: not email format
        request.setMessage(""); // Invalid: blank
        request.setPhone("123"); // Invalid: doesn't match Pattern
        request.setProfileId(null); // Invalid: null

        // Act & Assert
        mockMvc.perform(post("/api/v1/public/contact-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.message", containsString("name: Name is required")))
                .andExpect(jsonPath("$.message", containsString("email: Invalid email address")))
                .andExpect(jsonPath("$.message", containsString("message: Message is required")))
                .andExpect(jsonPath("$.message", containsString("phone: Invalid phone number")))
                .andExpect(jsonPath("$.message", containsString("profileId: Profile ID is required")));
    }
}
