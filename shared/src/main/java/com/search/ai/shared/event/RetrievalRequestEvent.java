package com.search.ai.shared.event;

import java.util.List;

public record RetrievalRequestEvent(
        String correlationId,
        String originalQuery,
        List<String> variants,
        int topK
) {}
