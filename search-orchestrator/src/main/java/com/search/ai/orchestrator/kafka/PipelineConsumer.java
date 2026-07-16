package com.search.ai.orchestrator.kafka;

import com.search.ai.orchestrator.config.PipelineProperties;
import com.search.ai.orchestrator.config.RedisConfig;
import com.search.ai.orchestrator.constants.PipelineConstants;
import com.search.ai.orchestrator.service.PipelineState;
import com.search.ai.orchestrator.service.PipelineStateStore;
import com.search.ai.shared.constant.TopicConstants;
import com.search.ai.shared.event.PipelineFailureEvent;
import com.search.ai.shared.event.QueryExpandedEvent;
import com.search.ai.shared.event.RankRequestEvent;
import com.search.ai.shared.event.RankResultEvent;
import com.search.ai.shared.event.RetrievalRequestEvent;
import com.search.ai.shared.event.RetrievalResultEvent;
import com.search.ai.shared.model.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PipelineConsumer {

    private final PipelineStateStore stateStore;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PipelineProperties properties;

    @KafkaListener(topics = TopicConstants.PROP_TOPIC_QUERY_EXPANDED)
    public void onQueryExpanded(QueryExpandedEvent event) {
        log.info("Received query expansion for correlationId: {}", event.correlationId());
        PipelineState state = getWithRetry(event.correlationId());
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
        PipelineState state = getWithRetry(event.correlationId());
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
        log.info("Received final rank results for correlationId: {}. Broadcasting via Redis.", event.correlationId());
        
        // Instead of local completion, broadcast via Redis Pub/Sub for distributed coordination
        SearchResponse response = new SearchResponse(event.ranked());
        redisTemplate.convertAndSend(RedisConfig.RESPONSE_CHANNEL, 
                new RedisResponseListener.PipelineResponse(event.correlationId(), response));
    }

    @KafkaListener(topics = TopicConstants.PROP_TOPIC_PIPELINE_FAILURE)
    public void onPipelineFailure(PipelineFailureEvent event) {
        log.error("Pipeline failure event for correlationId: {} at stage: {}. Error: {}", 
                event.correlationId(), event.stage(), event.error());
        
        // Broadcast failure or complete with empty results/error flag
        // For simplicity, we broadcast an empty response which the listener will process
        SearchResponse failureResponse = new SearchResponse(java.util.List.of()); 
        redisTemplate.convertAndSend(RedisConfig.RESPONSE_CHANNEL, 
                new RedisResponseListener.PipelineResponse(event.correlationId(), failureResponse));
    }

    /**
     * Resolves race conditions by retrying fetching state if not found immediately.
     */
    private PipelineState getWithRetry(String correlationId) {
        int retries = 3;
        while (retries > 0) {
            PipelineState state = stateStore.get(correlationId);
            if (state != null) {
                return state;
            }

            log.warn("State not found in Redis for correlationId: {}. Retrying... ({} left)", correlationId, retries - 1);
            
            try {
                Thread.sleep(100); // 100ms wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            retries--;
        }
        log.error("Failed to recover PipelineState from Redis for correlationId: {} after retries", correlationId);
        return null;
    }
}
