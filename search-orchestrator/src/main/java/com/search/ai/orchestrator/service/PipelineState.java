package com.search.ai.orchestrator.service;

import com.search.ai.shared.model.RankedDocument;
import com.search.ai.shared.model.UserContext;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
// import java.util.concurrent.CompletableFuture;

@Data
@Builder
public class PipelineState implements Serializable {
    private String correlationId;
    private String query;
    private UserContext userContext;
    private List<String> variants;
    private List<RankedDocument> finalResults;
    private String currentState; // e.g., ENRICHING, EXPANDING, RETRIEVING, RANKING, DONE
}
