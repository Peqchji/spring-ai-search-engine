package com.search.ai.shared.model;

import java.util.Map;

public record RankedDocument(
                String id,
                String content,
                Map<String, Object> metadata,
                double score) {
}
