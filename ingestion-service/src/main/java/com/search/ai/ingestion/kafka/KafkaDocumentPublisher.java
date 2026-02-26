package com.search.ai.ingestion.kafka;

import com.search.ai.shared.util.constants.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDocumentPublisher {

    @Value(AppConstants.PROP_KAFKA_TOPIC_RAW_DOCS)
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(List<Document> chunks) {
        for (Document chunk : chunks) {
            var payload = Map.of(
                    "id", chunk.getId(),
                    "content", chunk.getText(),
                    "metadata", chunk.getMetadata());

            kafkaTemplate.send(topic, chunk.getId(), payload);
        }
        log.info("Published {} chunks to topic '{}'", chunks.size(), topic);
    }
}
