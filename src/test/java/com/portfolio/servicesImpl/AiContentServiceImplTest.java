package com.portfolio.servicesImpl;

import com.portfolio.dtos.Ai.ContentDraftRequest;
import com.portfolio.dtos.Ai.ContentDraftResponse;
import com.portfolio.enums.ContentDraftTypeEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Covers the three outcomes that matter most: no key configured never calls out to Anthropic at
 * all, a successful response's nested content[0].text is extracted correctly, and a transport
 * failure maps to a clean AI_SERVICE_ERROR instead of leaking a RestClientException.
 */
class AiContentServiceImplTest {

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final AiContentServiceImpl service = new AiContentServiceImpl(restTemplate);

    private ContentDraftRequest request(ContentDraftTypeEnum type, String prompt) {
        ContentDraftRequest req = new ContentDraftRequest();
        req.setContentType(type);
        req.setPrompt(prompt);
        return req;
    }

    @Test
    void draftContent_noApiKeyConfigured_throwsWithoutCallingAnthropic() {
        ReflectionTestUtils.setField(service, "apiKey", "");

        assertThatThrownBy(() -> service.draftContent(request(ContentDraftTypeEnum.BIO, "backend engineer, 5 years Java")))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.AI_SERVICE_NOT_CONFIGURED);

        verifyNoInteractions(restTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void draftContent_successfulResponse_extractsNestedText() throws Exception {
        ReflectionTestUtils.setField(service, "apiKey", "sk-ant-test-key");
        ReflectionTestUtils.setField(service, "model", "claude-sonnet-5");

        Map<String, Object> anthropicResponse = Map.of(
                "content", List.of(Map.of("type", "text", "text", "  A polished bio.  "))
        );
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(anthropicResponse));

        ContentDraftResponse response = service.draftContent(request(ContentDraftTypeEnum.BIO, "backend engineer, 5 years Java"));

        assertThat(response.getDraft()).isEqualTo("A polished bio.");
    }

    @Test
    void draftContent_requestBuildingFailsUnexpectedly_mapsToAiServiceErrorNotRawException() {
        // Regression test: model left unset (null) used to escape as a raw NullPointerException
        // from Map.of(...) instead of the GenericException(AI_SERVICE_ERROR) every other failure
        // path here produces, because that request-building code originally ran outside the
        // try/catch.
        ReflectionTestUtils.setField(service, "apiKey", "sk-ant-test-key");
        ReflectionTestUtils.setField(service, "model", null);

        assertThatThrownBy(() -> service.draftContent(request(ContentDraftTypeEnum.BIO, "backend engineer")))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.AI_SERVICE_ERROR);

        verifyNoInteractions(restTemplate);
    }

    @Test
    void draftContent_transportFailure_mapsToAiServiceError() {
        ReflectionTestUtils.setField(service, "apiKey", "sk-ant-test-key");
        ReflectionTestUtils.setField(service, "model", "claude-sonnet-5");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> service.draftContent(request(ContentDraftTypeEnum.PROJECT_DESCRIPTION, "a react dashboard")))
                .isInstanceOf(GenericException.class)
                .extracting(e -> ((GenericException) e).getExceptionCode())
                .isEqualTo(ExceptionCodeEnum.AI_SERVICE_ERROR);
    }
}
