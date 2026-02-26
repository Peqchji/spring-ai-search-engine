package com.search.ai.shared.event;

public record QueryExpandEvent(
        String correlationId,
        String query
) {}
