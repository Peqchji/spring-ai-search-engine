package com.search.ai.expansion.kafka;

import com.search.ai.expansion.service.ExpansionService;
import com.search.ai.shared.event.QueryExpandEvent;
import com.search.ai.shared.event.QueryExpandedEvent;
import com.search.ai.shared.constant.TopicConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryExpansionConsumer {

    private final ExpansionService expansionService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TopicConstants.QUERY_EXPAND, groupId = "query-expansion-group")
    public void consume(QueryExpandEvent event) {
        log.info("Received query.expand event for correlationId: {}", event.correlationId());
        
        List<String> variants = expansionService.expand(event.query(), event.userContext());
        
        QueryExpandedEvent expandedEvent = new QueryExpandedEvent(
                event.correlationId(), 
                event.query(), 
                variants
        );
        
        kafkaTemplate.send(TopicConstants.QUERY_EXPANDED, expandedEvent);
        log.info("Sent query.expanded event for correlationId: {}", event.correlationId());
    }
}
