package com.search.ai.shared.event;

import com.search.ai.shared.model.RankedDocument;

import java.util.List;

public record RerankResultEvent(
        String correlationId,
        List<RankedDocument> ranked
) {}
