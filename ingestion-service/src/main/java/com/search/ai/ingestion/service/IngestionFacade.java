package com.search.ai.ingestion.service;

import com.search.ai.ingestion.model.IngestionMetadata;
import com.search.ai.ingestion.model.IngestionStatus;
import com.search.ai.ingestion.repository.IngestionMetadataRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionFacade {

        private final IngestionMetadataRepository metadataRepository;
        private final AsyncIngestionWorker asyncIngestionWorker;

        /**
         * Orchestrates the full ingestion pipeline using the Asynchronous Job Pattern.
         * The file is spooled to disk, a job record is defined, and the heavy Tika
         * parsing,
         * embedding, and database storing is offloaded to a background thread.
         */
        @Transactional
        public IngestionResult ingest(MultipartFile file) {
                log.info("Accepting file for ingestion: {} ({})", file.getOriginalFilename(), file.getContentType());

                try {
                        // 1. Spool file to disk (Zero-Copy) to avoid RAM exhaustion
                        Path tempFile = Files.createTempFile("async-ingest-", "-" + file.getOriginalFilename());
                        file.transferTo(tempFile);
                        log.info("Spooled {} bytes to disk: {}", file.getSize(), tempFile);

                        // 2. Create tracking record (PENDING)
                        IngestionMetadata metadata = IngestionMetadata.builder()
                                        .filename(file.getOriginalFilename())
                                        .contentType(file.getContentType())
                                        .documentCount(0) // Known later
                                        .chunkCount(0) // Known later
                                        .ingestedAt(LocalDateTime.now())
                                        .status(IngestionStatus.PENDING)
                                        .build();

                        metadata = metadataRepository.save(metadata);

                        // 3. Dispatch Background Job
                        asyncIngestionWorker.processIngestion(metadata.getId(), tempFile, file.getOriginalFilename());

                        // 4. Return immediately to the client
                        return new IngestionResult(
                                        metadata.getId(),
                                        file.getOriginalFilename(),
                                        "PENDING",
                                        "Ingestion job started in the background.");

                } catch (IOException e) {
                        log.error("Failed to spool upload to disk for async processing", e);
                        
                        throw new RuntimeException("Async I/O Error", e);
                }
        }

        public record IngestionResult(
                        String id,
                        String filename,
                        String status,
                        String message) {
        }
}
