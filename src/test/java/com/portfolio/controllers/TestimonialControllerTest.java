package com.portfolio.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dao.testimonial.TestimonialDao;
import com.portfolio.dtos.Common.BulkIdsRequest;
import com.portfolio.entities.Testimonial;
import com.portfolio.exceptions.GlobalExceptionHandler;
import com.portfolio.services.TestimonialService;
import com.portfolio.utils.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Covers the ownership check that was missing entirely on
 * {@code deleteTestimonial} (no Authorization header, no comparison against
 * the record's profileId — any authenticated user could delete any other
 * profile's testimonial by id) and its new bulk counterpart.
 */
@ExtendWith(MockitoExtension.class)
class TestimonialControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TestimonialService testimonialService;
    @Mock
    private TestimonialDao testimonialDao;
    @Mock
    private Helper helper;

    @InjectMocks
    private TestimonialController testimonialController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Long CALLER_PROFILE_ID = 1L;
    private static final Long OTHER_PROFILE_ID = 2L;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(testimonialController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(helper.getProfileIdFromHeader(any())).thenReturn(CALLER_PROFILE_ID);
    }

    private Testimonial testimonialOwnedBy(Long id, Long profileId) {
        return Testimonial.builder().id(id).profileId(profileId).name("Jane").build();
    }

    @Test
    void deleteTestimonial_NotOwner_Returns403Forbidden() throws Exception {
        when(testimonialDao.findById(10L)).thenReturn(Optional.of(testimonialOwnedBy(10L, OTHER_PROFILE_ID)));

        mockMvc.perform(delete("/api/v1/testimonials/10")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"));

        verify(testimonialService, never()).deleteById(any());
    }

    @Test
    void deleteTestimonial_Owner_DeletesSuccessfully() throws Exception {
        when(testimonialDao.findById(10L)).thenReturn(Optional.of(testimonialOwnedBy(10L, CALLER_PROFILE_ID)));

        mockMvc.perform(delete("/api/v1/testimonials/10")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());

        verify(testimonialService).deleteById(10L);
    }

    @Test
    void bulkDeleteTestimonials_FiltersOutIdsNotOwnedByCaller() throws Exception {
        when(testimonialDao.findById(10L)).thenReturn(Optional.of(testimonialOwnedBy(10L, CALLER_PROFILE_ID)));
        when(testimonialDao.findById(20L)).thenReturn(Optional.of(testimonialOwnedBy(20L, OTHER_PROFILE_ID)));
        when(testimonialService.bulkDeleteByIds(eq(List.of(10L)))).thenReturn(1);

        BulkIdsRequest request = new BulkIdsRequest();
        request.setIds(List.of(10L, 20L));

        mockMvc.perform(delete("/api/v1/testimonials/bulk")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));

        // Only the owned id (10) should ever reach the service — id 20
        // belongs to another profile and must be silently dropped, not deleted.
        verify(testimonialService).bulkDeleteByIds(List.of(10L));
    }
}
