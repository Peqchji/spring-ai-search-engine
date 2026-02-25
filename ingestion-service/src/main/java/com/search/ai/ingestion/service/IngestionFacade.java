package com.search.ai.ingestion.service;

import com.search.ai.ingestion.kafka.KafkaDocumentPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionFacade {

    private final DocumentLoaderService documentLoaderService;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final KafkaDocumentPublisher kafkaDocumentPublisher;

    /**
     * Orchestrates the full ingestion pipeline:
     * Load → Chunk → Embed + Store → Publish to Kafka
     */
    public IngestionResult ingest(MultipartFile file) {
        log.info("Ingesting file: {} ({})", file.getOriginalFilename(), file.getContentType());

        // 1. Load — parse file into documents
        List<Document> documents = documentLoaderService.load(file);
        log.info("Loaded {} document(s) from file", documents.size());

        // 2. Chunk — split into overlapping chunks
        List<Document> chunks = chunkingService.chunk(documents);
        log.info("Chunked into {} chunk(s)", chunks.size());

        // 3. Embed + Store — generate embeddings and store in Qdrant
        embeddingService.embedAndStore(chunks);
        log.info("Embedded and stored {} chunk(s) in Qdrant", chunks.size());

        // 4. Publish — send to Kafka for downstream consumers
        kafkaDocumentPublisher.publish(chunks);
        log.info("Published {} chunk(s) to Kafka", chunks.size());

        return new IngestionResult(
                file.getOriginalFilename(),
                documents.size(),
                chunks.size());
    }

    public record IngestionResult(
            String filename,
            int documentsLoaded,
            int chunksCreated) {
    }
}
