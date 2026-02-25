package com.search.ai.ingestion.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDocumentPublisher {

    private static final String TOPIC = "raw-docs";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(List<Document> chunks) {
        for (Document chunk : chunks) {
            var payload = Map.of(
                    "id", chunk.getId(),
                    "content", chunk.getText(),
                    "metadata", chunk.getMetadata()
            );
            kafkaTemplate.send(TOPIC, chunk.getId(), payload);
        }
        log.info("Published {} chunks to topic '{}'", chunks.size(), TOPIC);
    }
}
