package com.search.ai.orchestrator.kafka;

import com.search.ai.shared.constant.TopicConstants;
import com.search.ai.orchestrator.config.PipelineProperties;
import com.search.ai.orchestrator.constants.PipelineConstants;
import com.search.ai.orchestrator.service.PipelineOrchestrator;
import com.search.ai.orchestrator.service.PipelineState;
import com.search.ai.orchestrator.service.PipelineStateStore;
import com.search.ai.shared.event.QueryExpandedEvent;
import com.search.ai.shared.event.RankRequestEvent;
import com.search.ai.shared.event.RankResultEvent;
import com.search.ai.shared.event.RetrievalRequestEvent;
import com.search.ai.shared.event.RetrievalResultEvent;
import com.search.ai.shared.model.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PipelineConsumer {

    private final PipelineOrchestrator orchestrator;
    private final PipelineStateStore stateStore;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PipelineProperties properties;

    @KafkaListener(topics = TopicConstants.PROP_TOPIC_QUERY_EXPANDED)
    public void onQueryExpanded(QueryExpandedEvent event) {
        log.info("Received query expansion for correlationId: {}", event.correlationId());
        PipelineState state = stateStore.get(event.correlationId());

        if (state == null) return;

        state.setVariants(event.variants());
        state.setCurrentState(PipelineConstants.States.RETRIEVING);
        stateStore.save(event.correlationId(), state);

        kafkaTemplate.send(
            properties.getTopics().getRetrievalRequest(), 
            event.correlationId(),
            new RetrievalRequestEvent(
                event.correlationId(), 
                event.originalQuery(), 
                event.variants(), 
                PipelineConstants.Fallbacks.DEFAULT_TOP_K, 
                state.getUserContext()
            )
        );
    }

    @KafkaListener(topics = TopicConstants.PROP_TOPIC_RETRIEVAL_RESULTS)
    public void onRetrievalResults(RetrievalResultEvent event) {
        log.info("Received retrieval results for correlationId: {}", event.correlationId());
        PipelineState state = stateStore.get(event.correlationId());
        if (state == null) return;

        state.setCurrentState(PipelineConstants.States.RANKING);
        stateStore.save(event.correlationId(), state);

        kafkaTemplate.send(
            properties.getTopics().getRankRequest(), 
            event.correlationId(),
            new RankRequestEvent(
                event.correlationId(), 
                state.getQuery(), 
                event.candidates(), 
                state.getUserContext()
            )
        );
    }

    @KafkaListener(topics = TopicConstants.PROP_TOPIC_RANK_RESULTS)
    public void onRankResults(RankResultEvent event) {
        log.info("Received final rank results for correlationId: {}", event.correlationId());

        orchestrator.completeRequest(
            event.correlationId(), 
            new SearchResponse(event.ranked())
        );
    }
}
