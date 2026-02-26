package com.search.ai.ingestion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
public class DocumentLoaderService {

    /**
     * Reads a document from a stable physical path on disk.
     * This allows the file to be parsed asynchronously, detached from the original
     * HTTP Request.
     */
    public List<Document> load(Path filePath, String originalFilename) {
        log.info("Loading document: {}", originalFilename);

        try {
            // Hand over the physical file to Tika using Spring's FileSystemResource
            var resource = new FileSystemResource(filePath.toFile());
            var reader = new TikaDocumentReader(resource);

            return reader.get();

        } catch (Exception e) {
            log.error("Failed to process file: {}", originalFilename, e);
            throw new RuntimeException("File I/O error", e);
        }
    }
}
