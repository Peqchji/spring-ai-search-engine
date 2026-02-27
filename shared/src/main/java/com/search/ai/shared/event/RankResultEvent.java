package com.search.ai.shared.event;

import com.search.ai.shared.model.RankedDocument;

import java.util.List;

public record RankResultEvent(
        String correlationId,
        List<RankedDocument> ranked) {
}
