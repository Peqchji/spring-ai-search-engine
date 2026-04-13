package com.search.ai.shared.model;

public record DocumentCandidate(
                String id,
                double score,
                String source) {
}
