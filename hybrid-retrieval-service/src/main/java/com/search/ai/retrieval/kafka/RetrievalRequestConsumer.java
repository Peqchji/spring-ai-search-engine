package com.search.ai.retrieval.kafka;

import com.search.ai.retrieval.service.HybridSearchService;
import com.search.ai.shared.event.RetrievalRequestEvent;
import com.search.ai.shared.event.RetrievalResultEvent;
import com.search.ai.shared.constant.TopicConstants;
import com.search.ai.shared.model.DocumentCandidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetrievalRequestConsumer {

    private final HybridSearchService hybridSearchService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TopicConstants.RETRIEVAL_REQUEST, groupId = "hybrid-retrieval-group")
    public void consume(RetrievalRequestEvent event) {
        log.info("Received retrieval.request for correlationId: {}", event.correlationId());
        
        List<DocumentCandidate> candidates = hybridSearchService.search(
                event.originalQuery(), 
                event.variants(), 
                event.topK(), 
                event.userContext()
        );
        
        RetrievalResultEvent resultEvent = new RetrievalResultEvent(event.correlationId(), candidates);
        kafkaTemplate.send(TopicConstants.RETRIEVAL_RESULTS, resultEvent);
        
        log.info("Sent retrieval.results for correlationId: {}", event.correlationId());
    }
}
