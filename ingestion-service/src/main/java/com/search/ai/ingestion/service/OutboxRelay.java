package com.search.ai.ingestion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.ai.ingestion.kafka.KafkaDocumentPublisher;
import com.search.ai.ingestion.model.LexicalDocument;
import com.search.ai.ingestion.model.OutboxEvent;
import com.search.ai.shared.model.DocumentEventDTO;
import com.search.ai.ingestion.repository.LexicalRepository;
import com.search.ai.ingestion.repository.OutboxRepository;
import com.search.ai.shared.util.constants.AppConstants;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.data.mongodb.core.messaging.Message;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Subscription;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final LexicalRepository lexicalRepository;
    private final KafkaDocumentPublisher kafkaDocumentPublisher;
    private final ObjectMapper objectMapper;
    private final MessageListenerContainer container;

    @PostConstruct
    public void startWatching() {
        log.info("Initializing MongoDB Watch on collection: {}",
                AppConstants.COLLECTION_OUTBOX);
        ChangeStreamRequest<OutboxEvent> request = ChangeStreamRequest.builder(
                (Message<ChangeStreamDocument<org.bson.Document>, OutboxEvent> message) -> {
                    log.info("Captured event from Mongo Watch: {}", message.getBody().getId());
                    processEvent(message.getBody());
                })
                .collection(AppConstants.COLLECTION_OUTBOX)
                .build();

        if (!container.isRunning()) {
            container.start();
        }

        Subscription subscription = container.register(request, OutboxEvent.class);
        log.info("MongoDB Change Stream (Watch) started. Active: {}", subscription.isActive());
    }

    private void processEvent(OutboxEvent event) {
        if (event == null || event.isProcessed()) {
            return;
        }

        try {
            log.info("Processing outbox event: {} of type: {}", event.getId(), event.getType());

            if (AppConstants.EVENT_TYPE_INGESTION_COMPLETED.equals(event.getType())) {
                List<DocumentEventDTO> dtos = objectMapper.readValue(
                        event.getPayload(),
                        new TypeReference<List<DocumentEventDTO>>() {
                        });

                // 1. Map back to Spring AI Documents for Kafka
                List<Document> chunks = dtos.stream()
                        .map(dto -> new Document(dto.getId(), dto.getContent(), dto.getMetadata()))
                        .toList();

                // 2. Publish to Kafka
                kafkaDocumentPublisher.publish(chunks);

                // 3. Index to Elasticsearch for Lexical Search
                indexToElasticsearch(dtos);

                event.setProcessed(true);
                event.setProcessedAt(LocalDateTime.now());
                outboxRepository.save(event);

                log.info("Successfully relayed outbox event ID: {} in real-time", event.getId());
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize outbox payload for event ID: {}", event.getId(), e);
        } catch (Exception e) {
            log.error("Error while relaying outbox event ID: {}", event.getId(), e);
        }
    }

    private void indexToElasticsearch(List<DocumentEventDTO> dtos) {
        List<LexicalDocument> lexicalDocs = dtos.stream()
                .map(dto -> LexicalDocument.builder()
                        .id(dto.getId())
                        .content(dto.getContent())
                        .metadata(dto.getMetadata())
                        .indexedAt(LocalDateTime.now())
                        .build())
                .toList();

        lexicalRepository.saveAll(lexicalDocs);
        log.info("Indexed {} chunks to Elasticsearch", lexicalDocs.size());
    }
}
