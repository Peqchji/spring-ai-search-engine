package com.search.ai.ranker.kafka;

import com.search.ai.ranker.service.DocumentRankerService;
import com.search.ai.shared.event.RankRequestEvent;
import com.search.ai.shared.event.RankResultEvent;
import com.search.ai.shared.constant.TopicConstants;
import com.search.ai.shared.model.RankedDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankRequestConsumer {

    private final DocumentRankerService documentRankerService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = TopicConstants.RANK_REQUEST, groupId = "ranker-group")
    public void consume(RankRequestEvent event) {
        log.info("Received rank.request for correlationId: {}", event.correlationId());
        
        List<RankedDocument> ranked = documentRankerService.rank(
                event.query(), 
                event.candidates(), 
                event.userContext()
        );
        
        RankResultEvent resultEvent = new RankResultEvent(event.correlationId(), ranked);
        kafkaTemplate.send(TopicConstants.RANK_RESULTS, resultEvent);
        
        log.info("Sent rank.results for correlationId: {}", event.correlationId());
    }
}
