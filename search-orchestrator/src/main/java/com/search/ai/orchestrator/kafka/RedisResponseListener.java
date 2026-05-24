package com.search.ai.orchestrator.kafka;

import com.search.ai.orchestrator.config.RedisConfig;
import com.search.ai.orchestrator.service.PipelineOrchestrator;
import com.search.ai.shared.model.SearchResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.Serializable;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisResponseListener implements MessageListener {

    private final RedisMessageListenerContainer container;
    private final PipelineOrchestrator orchestrator;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @PostConstruct
    public void init() {
        container.addMessageListener(this, topic);
        log.info("Subscribed to Redis channel: {}", RedisConfig.RESPONSE_CHANNEL);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            PipelineResponse response = (PipelineResponse) redisTemplate.getValueSerializer().deserialize(message.getBody());
            
            if (response != null) {
                log.info("Received Redis broadcast for correlationId: {}", response.getCorrelationId());
                orchestrator.completeRequest(response.getCorrelationId(), response.getResponse());
            }
        } catch (Exception e) {
            log.error("Failed to process Redis broadcast message", e);
        }
    }

    /**
     * Internal DTO for broadcasting results via Redis.
     */
    @Data
    public static class PipelineResponse implements Serializable {
        private String correlationId;
        private SearchResponse response;

        public PipelineResponse() {}

        public PipelineResponse(String correlationId, SearchResponse response) {
            this.correlationId = correlationId;
            this.response = response;
        }
    }
}
