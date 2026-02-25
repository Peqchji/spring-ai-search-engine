package com.search.ai.ingestion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.ai.ingestion.model.IngestionMetadata;
import com.search.ai.ingestion.model.IngestionStatus;
import com.search.ai.ingestion.model.OutboxEvent;
import com.search.ai.ingestion.repository.IngestionMetadataRepository;
import com.search.ai.ingestion.repository.OutboxRepository;
import com.search.ai.shared.model.DocumentEventDTO;
import com.search.ai.shared.constant.APIMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncIngestionWorker {

    private final DocumentLoaderService documentLoaderService;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final IngestionMetadataRepository metadataRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.events.ingestion-completed:INGESTION_COMPLETED}")
    private String eventTypeIngestionCompleted;

    @Async
    @Transactional
    public void processIngestion(String metadataId, Path filePath, String originalFilename) {
        log.info("Starting background ingestion for trace id: {}", metadataId);

        IngestionMetadata metadata = metadataRepository.findById(metadataId)
                .orElseThrow(() -> new RuntimeException(APIMessages.ERROR_METADATA_NOT_FOUND + metadataId));

        metadata.setStatus(IngestionStatus.PROCESSING);
        metadata = metadataRepository.save(metadata);

        try {
            // 1. Load — parse file from disk into documents
            List<Document> documents = documentLoaderService.load(filePath, originalFilename);

            // 2. Chunk — split into overlapping chunks
            List<Document> chunks = chunkingService.chunk(documents);

            // 3. Embed + Store — generate embeddings and store in MongoDB
            embeddingService.embedAndStore(chunks);
            log.info("Embedded and stored {} chunk(s) in MongoDB", chunks.size());

            // 4. Persistence — Update metadata to COMPLETED
            metadata.setDocumentCount(documents.size());
            metadata.setChunkCount(chunks.size());
            metadata.setStatus(IngestionStatus.COMPLETED);
            metadataRepository.save(metadata);

            // 5. Outbox — Save event to be picked up by Relay
            saveToOutbox(metadata, chunks);

            log.info("Successfully completed async ingestion for id: {}", metadataId);
        } catch (Exception e) {
            log.error("Failed to process ingestion for id: {}", metadataId, e);
            metadata.setStatus(IngestionStatus.FAILED);
            metadataRepository.save(metadata);
        }
    }

    private void saveToOutbox(IngestionMetadata metadata, List<Document> chunks) throws JsonProcessingException {
        List<DocumentEventDTO> dtos = chunks.stream()
                .map(chunk -> DocumentEventDTO.builder()
                        .id(chunk.getId())
                        .content(chunk.getText())
                        .metadata(chunk.getMetadata())
                        .build())
                .toList();

        OutboxEvent event = OutboxEvent.builder()
                .aggregateId(metadata.getId())
                .type(eventTypeIngestionCompleted)
                .payload(objectMapper.writeValueAsString(dtos))
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();

        outboxRepository.save(event);
    }
}
