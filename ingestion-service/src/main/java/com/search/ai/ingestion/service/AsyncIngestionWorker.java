package com.search.ai.ingestion.service;

import com.search.ai.ingestion.model.IngestionMetadata;
import com.search.ai.ingestion.model.IngestionStatus;
import com.search.ai.ingestion.repository.IngestionMetadataRepository;
import com.search.ai.shared.util.constants.AppConstants;
import com.search.ai.shared.constant.APIMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncIngestionWorker {

    private final DocumentLoaderService documentLoaderService;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final IngestionMetadataRepository metadataRepository;

    @Value(AppConstants.PROP_EVENT_INGESTION_COMPLETED)
    private String eventTypeIngestionCompleted;

    @Async
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

            // 3. Embed + Store — generate embeddings and store in MongoDB via Spring AI
            embeddingService.embedAndStore(chunks);
            log.info("Embedded and stored {} chunk(s) in MongoDB", chunks.size());

            // 4. Persistence — Update metadata to COMPLETED
            metadata.setDocumentCount(documents.size());
            metadata.setChunkCount(chunks.size());
            metadata.setStatus(IngestionStatus.COMPLETED);
            metadataRepository.save(metadata);

            log.info("Successfully completed async ingestion for id: {}", metadataId);
        } catch (Exception e) {
            log.error("Failed to process ingestion for id: {}", metadataId, e);
            metadata.setStatus(IngestionStatus.FAILED);
            metadataRepository.save(metadata);
        }
    }
}
