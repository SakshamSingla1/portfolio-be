package com.portfolio.servicesImpl;

import com.portfolio.config.AppConfig;
import com.portfolio.dtos.Ai.ContentDraftRequest;
import com.portfolio.dtos.Ai.ContentDraftResponse;
import com.portfolio.enums.ContentDraftTypeEnum;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.AiContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiContentServiceImpl implements AiContentService {

    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final int MAX_TOKENS = 400;

    private final RestTemplate aiRestTemplate;

    @Value("${anthropic.api-key:}")
    private String apiKey;

    @Value("${anthropic.model:claude-sonnet-5}")
    private String model;

    public AiContentServiceImpl(@AppConfig.AiRestTemplate RestTemplate aiRestTemplate) {
        this.aiRestTemplate = aiRestTemplate;
    }

    @Override
    public ContentDraftResponse draftContent(ContentDraftRequest request) throws GenericException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new GenericException(ExceptionCodeEnum.AI_SERVICE_NOT_CONFIGURED,
                    "The AI writing assistant isn't configured yet -- set an Anthropic API key to enable it");
        }

        try {
            String userPrompt = buildPrompt(request.getContentType(), request.getPrompt());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", ANTHROPIC_VERSION);

            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", MAX_TOKENS,
                    "messages", List.of(Map.of("role", "user", "content", userPrompt))
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = aiRestTemplate.exchange(
                    ANTHROPIC_API_URL, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class
            ).getBody();

            String draft = extractText(response);
            if (draft == null || draft.isBlank()) {
                throw new GenericException(ExceptionCodeEnum.AI_SERVICE_ERROR, "The AI assistant returned an empty draft");
            }
            return new ContentDraftResponse(draft.trim());
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            // Not just RestClientException: a malformed/unavailable response, an unexpected null
            // in the config, etc. should all map to the same clean error rather than any of them
            // leaking a raw, unwrapped exception past this boundary.
            log.error("Anthropic API call failed for contentType={}", request.getContentType(), e);
            throw new GenericException(ExceptionCodeEnum.AI_SERVICE_ERROR, "Failed to generate a draft, please try again");
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) return null;
        Object content = response.get("content");
        if (!(content instanceof List<?> blocks) || blocks.isEmpty()) return null;
        Object first = blocks.get(0);
        if (!(first instanceof Map<?, ?> block)) return null;
        Object text = ((Map<String, Object>) block).get("text");
        return text != null ? text.toString() : null;
    }

    private String buildPrompt(ContentDraftTypeEnum contentType, String notes) {
        String instruction = switch (contentType) {
            case BIO -> "Write a concise, engaging portfolio \"About Me\" bio in first person, "
                    + "2-4 sentences, professional but approachable tone.";
            case PROJECT_DESCRIPTION -> "Write a concise portfolio project description, 2-4 sentences, "
                    + "highlighting what the project does and the impact/result, professional tone.";
            case EXPERIENCE_DESCRIPTION -> "Write 3-5 concise, resume-style bullet points (one per line, "
                    + "no bullet symbols) describing this work experience, each starting with an action verb.";
        };
        return instruction
                + " Base it on these rough notes from the user (expand and polish them, don't just repeat them verbatim): \""
                + notes.trim() + "\". "
                + "Return ONLY the finished text with no preamble, no markdown formatting, and no quotation marks around it.";
    }
}
