package com.search.ai.shared.model;

public record DocumentCandidate(
        String id,
        String content,
        double score,
        String source
) {}
