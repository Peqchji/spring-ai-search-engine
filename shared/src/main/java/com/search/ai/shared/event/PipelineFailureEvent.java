package com.search.ai.shared.event;

import java.time.Instant;

/**
 * Event published when a stage in the search pipeline fails.
 * This allows the orchestrator to resolve the user request gracefully
 * instead of waiting for a timeout.
 */
public record PipelineFailureEvent(
    String correlationId,
    String stage,
    String error,
    Instant timestamp
) {
    public PipelineFailureEvent(String correlationId, String stage, String error) {
        this(correlationId, stage, error, Instant.now());
    }
}
