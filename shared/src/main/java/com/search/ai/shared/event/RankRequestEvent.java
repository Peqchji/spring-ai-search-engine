package com.search.ai.shared.event;

import com.search.ai.shared.model.DocumentCandidate;

import java.util.List;

public record RankRequestEvent(
        String correlationId,
        String query,
        List<DocumentCandidate> candidates) {
}
