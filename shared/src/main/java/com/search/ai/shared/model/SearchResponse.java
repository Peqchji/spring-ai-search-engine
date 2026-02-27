package com.search.ai.shared.model;

import java.util.List;

public record SearchResponse(
                List<RankedDocument> ranked) {
}
