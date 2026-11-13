package com.portfolio.services;

import com.portfolio.dtos.Ai.ContentDraftRequest;
import com.portfolio.dtos.Ai.ContentDraftResponse;
import com.portfolio.exceptions.GenericException;

public interface AiContentService {

    /** Drafts bio/project/experience copy from the user's rough notes via Claude. Throws
     * GenericException(AI_SERVICE_NOT_CONFIGURED) if no API key is set. */
    ContentDraftResponse draftContent(ContentDraftRequest request) throws GenericException;
}
