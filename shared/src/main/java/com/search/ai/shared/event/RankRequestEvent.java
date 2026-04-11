package com.search.ai.shared.event;

import com.search.ai.shared.model.DocumentCandidate;
import com.search.ai.shared.model.UserContext;

import java.util.List;

public record RankRequestEvent(
        String correlationId,
        String query,
        List<DocumentCandidate> candidates,
        UserContext userContext) {
}
