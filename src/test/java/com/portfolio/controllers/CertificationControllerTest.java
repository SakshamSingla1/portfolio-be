package com.portfolio.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.dao.certification.CertificationDao;
import com.portfolio.dtos.Common.BulkIdsRequest;
import com.portfolio.entities.Certifications;
import com.portfolio.exceptions.GlobalExceptionHandler;
import com.portfolio.services.CertificationService;
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
 * The single-delete endpoint already checked ownership before this change;
 * this covers that the new bulk endpoint enforces the same per-id ownership
 * filter rather than trusting the caller-supplied id list wholesale.
 */
@ExtendWith(MockitoExtension.class)
class CertificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CertificationService certificationService;
    @Mock
    private CertificationDao certificationDao;
    @Mock
    private Helper helper;

    @InjectMocks
    private CertificationController certificationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Long CALLER_PROFILE_ID = 1L;
    private static final Long OTHER_PROFILE_ID = 2L;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(certificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(helper.getProfileIdFromHeader(any())).thenReturn(CALLER_PROFILE_ID);
    }

    private Certifications certificationOwnedBy(Long id, Long profileId) {
        return Certifications.builder().id(id).profileId(profileId).title("AWS").build();
    }

    @Test
    void bulkDeleteCertifications_FiltersOutIdsNotOwnedByCaller() throws Exception {
        when(certificationDao.findById(10L)).thenReturn(Optional.of(certificationOwnedBy(10L, CALLER_PROFILE_ID)));
        when(certificationDao.findById(20L)).thenReturn(Optional.of(certificationOwnedBy(20L, OTHER_PROFILE_ID)));
        when(certificationService.bulkDeleteByIds(eq(List.of(10L)))).thenReturn(1);

        BulkIdsRequest request = new BulkIdsRequest();
        request.setIds(List.of(10L, 20L));

        mockMvc.perform(delete("/api/v1/certifications/bulk")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));

        verify(certificationService).bulkDeleteByIds(List.of(10L));
    }

    @Test
    void bulkDeleteCertifications_UnknownId_IsSkippedNotError() throws Exception {
        when(certificationDao.findById(99L)).thenReturn(Optional.empty());
        when(certificationService.bulkDeleteByIds(eq(List.<Long>of()))).thenReturn(0);

        BulkIdsRequest request = new BulkIdsRequest();
        request.setIds(List.of(99L));

        mockMvc.perform(delete("/api/v1/certifications/bulk")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0));
    }
}
