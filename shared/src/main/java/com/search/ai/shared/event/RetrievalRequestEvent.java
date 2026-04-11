package com.search.ai.shared.event;

import com.search.ai.shared.model.UserContext;

import java.util.List;

public record RetrievalRequestEvent(
        String correlationId,
        String originalQuery,
        List<String> variants,
        int topK,
        UserContext userContext) {
}
