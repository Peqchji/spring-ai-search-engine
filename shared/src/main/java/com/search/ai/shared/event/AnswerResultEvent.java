package com.search.ai.shared.event;

import com.search.ai.shared.model.SourceExcerpt;

import java.util.List;

public record AnswerResultEvent(
        String correlationId,
        String answer,
        List<SourceExcerpt> sources
) {}
