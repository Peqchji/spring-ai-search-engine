package com.search.ai.ingestion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.ai.ingestion.model.IngestionMetadata;
import com.search.ai.ingestion.model.IngestionStatus;
import com.search.ai.ingestion.model.OutboxEvent;
import com.search.ai.ingestion.repository.IngestionMetadataRepository;
import com.search.ai.ingestion.repository.OutboxRepository;
import com.search.ai.shared.util.constants.AppConstants;
import com.search.ai.shared.model.DocumentEventDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionFacade {

    private final DocumentLoaderService documentLoaderService;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;

    private final IngestionMetadataRepository metadataRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Orchestrates the full ingestion pipeline using the Outbox Pattern.
     * Consistency is guaranteed between DB state and eventual Kafka message.
     */
    @Transactional
    public IngestionResult ingest(MultipartFile file) {
        log.info("Ingesting file: {} ({})", file.getOriginalFilename(), file.getContentType());

        // 1. Load — parse file into documents
        List<Document> documents = documentLoaderService.load(file);

        // 2. Chunk — split into overlapping chunks
        List<Document> chunks = chunkingService.chunk(documents);

        // 3. Embed + Store — generate embeddings and store in MongoDB
        // MongoDB is the primary Vector Store for semantic search.
        embeddingService.embedAndStore(chunks);
        log.info("Embedded and stored {} chunk(s) in MongoDB", chunks.size());

        // 4. Persistence — Save metadata to MongoDB
        IngestionMetadata metadata = IngestionMetadata.builder()
                .filename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .documentCount(documents.size())
                .chunkCount(chunks.size())
                .ingestedAt(LocalDateTime.now())
                .status(IngestionStatus.COMPLETED)
                .build();

        metadata = metadataRepository.save(metadata);

        // 5. Outbox — Save event to be picked up by Relay
        saveToOutbox(metadata, chunks);

        return new IngestionResult(
                file.getOriginalFilename(),
                documents.size(),
                chunks.size());
    }

    private void saveToOutbox(IngestionMetadata metadata, List<Document> chunks) {
        try {
            List<DocumentEventDTO> dtos = chunks.stream()
                    .map(chunk -> DocumentEventDTO.builder()
                            .id(chunk.getId())
                            .content(chunk.getText())
                            .metadata(chunk.getMetadata())
                            .build())
                    .toList();

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(metadata.getId())
                    .type(AppConstants.EVENT_TYPE_INGESTION_COMPLETED)
                    .payload(objectMapper.writeValueAsString(dtos))
                    .createdAt(LocalDateTime.now())
                    .processed(false)
                    .build();

            outboxRepository.save(event);

            log.info("Saved ingestion event to outbox for aggregate ID: {}", metadata.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize chunks for outbox", e);
            throw new RuntimeException("Outbox serialization error", e);
        }
    }

    public record IngestionResult(
            String filename,
            int documentsLoaded,
            int chunksCreated) {
    }
}
