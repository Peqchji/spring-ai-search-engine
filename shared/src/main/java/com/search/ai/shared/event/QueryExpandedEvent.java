package com.search.ai.shared.event;

import java.util.List;

public record QueryExpandedEvent(
        String correlationId,
        String originalQuery,
        List<String> variants
) {}
