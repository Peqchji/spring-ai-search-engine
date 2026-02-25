package com.search.ai.ingestion.controller;

import com.search.ai.ingestion.service.DocumentLoaderService;
import com.search.ai.ingestion.service.ChunkingService;
import com.search.ai.ingestion.service.EmbeddingService;
import com.search.ai.ingestion.kafka.KafkaDocumentPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class IngestionController {

    private final DocumentLoaderService documentLoaderService;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final KafkaDocumentPublisher kafkaDocumentPublisher;

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingest(@RequestParam("file") MultipartFile file) {
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

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "filename", file.getOriginalFilename(),
                "documentsLoaded", documents.size(),
                "chunksCreated", chunks.size()
        ));
    }
}
