package com.search.ai.ingestion.service;

import com.search.ai.ingestion.model.IngestionMetadata;
import com.search.ai.ingestion.model.IngestionStatus;
import com.search.ai.ingestion.repository.IngestionMetadataRepository;
import com.search.ai.shared.util.constants.AppConstants;

import com.search.ai.shared.constant.APIMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
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

        @Value(AppConstants.PROP_TEMP_FILE_PREFIX)
        private String tempFilePrefix;

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
                        Path tempFile = Files.createTempFile(tempFilePrefix,
                                        "-" + file.getOriginalFilename());
                        file.transferTo(tempFile);
                        log.info("Spooled {} bytes to disk: {}", file.getSize(), tempFile);

                        IngestionMetadata metadata = IngestionMetadata.builder()
                                        .filename(file.getOriginalFilename())
                                        .contentType(file.getContentType())
                                        .documentCount(0)
                                        .chunkCount(0)
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
                                        IngestionStatus.PENDING.name(),
                                        APIMessages.INGEST_ASYNC_STARTED);

                } catch (IOException e) {
                        log.error("Failed to spool upload to disk for async processing", e);

                        throw new RuntimeException(APIMessages.ERROR_ASYNC_IO, e);
                }
        }

        public record IngestionResult(
                        String id,
                        String filename,
                        String status,
                        String message) {
        }
}
