package com.search.ai.shared.event;

import com.search.ai.shared.model.RankedDocument;

import java.util.List;

public record AnswerRequestEvent(
        String correlationId,
        String query,
        List<RankedDocument> context
) {}
