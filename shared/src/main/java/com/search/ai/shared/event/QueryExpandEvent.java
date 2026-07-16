package com.search.ai.shared.event;

import com.search.ai.shared.model.UserContext;

public record QueryExpandEvent(
        String correlationId,
        String query,
        UserContext userContext) {
}
