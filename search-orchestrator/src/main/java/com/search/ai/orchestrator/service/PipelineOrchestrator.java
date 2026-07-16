package com.search.ai.orchestrator.service;

import com.search.ai.orchestrator.config.PipelineProperties;
import com.search.ai.orchestrator.constants.PipelineConstants;
import com.search.ai.shared.event.QueryExpandEvent;
import com.search.ai.shared.model.SearchResponse;
import com.search.ai.shared.model.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PipelineOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PipelineStateStore stateStore;
    private final UserContextEnricher userContextEnricher;
    private final PipelineProperties properties;

    private final Map<String, CompletableFuture<SearchResponse>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<SearchResponse> search(String query, UserContext userContext) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<SearchResponse> future = new CompletableFuture<>();

        future.orTimeout(properties.getTimeoutMs(), TimeUnit.MILLISECONDS)
            .whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Pipeline timeout for correlationId: {}", correlationId);
                    stateStore.delete(correlationId);
                }
                pendingRequests.remove(correlationId);
            });

        pendingRequests.put(correlationId, future);

        UserContext enriched = userContextEnricher.enrich(userContext);

        PipelineState state = PipelineState.builder()
                .correlationId(correlationId)
                .query(query)
                .userContext(enriched)
                .currentState(PipelineConstants.States.EXPANDING)
                .build();

        stateStore.save(correlationId, state);

        log.info("Starting pipeline for correlationId: {}, query: {}", correlationId, query);
        kafkaTemplate.send(
            properties.getTopics().getQueryExpand(), 
            correlationId, 
            new QueryExpandEvent(correlationId, query, enriched)
        );

        return future;
    }

    public void completeRequest(String correlationId, SearchResponse response) {
        CompletableFuture<SearchResponse> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(response);
            stateStore.delete(correlationId);
        }
    }

    public CompletableFuture<SearchResponse> getPendingRequest(String correlationId) {
        return pendingRequests.get(correlationId);
    }
}
