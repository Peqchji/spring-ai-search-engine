package com.search.ai.shared.model;

import java.util.List;

public record SearchResponse(
        String answer,
        List<SourceExcerpt> sources
) {}
