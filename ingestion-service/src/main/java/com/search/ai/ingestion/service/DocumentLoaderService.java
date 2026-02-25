package com.search.ai.ingestion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class DocumentLoaderService {

    public List<Document> load(MultipartFile file) {
        log.info("Loading document: {}", file.getOriginalFilename());

        try (InputStream is = new BufferedInputStream(file.getInputStream())) {
            var resource = new InputStreamResource(is) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            var reader = new TikaDocumentReader(resource);
            return reader.get();
        } catch (IOException e) {
            log.error("Failed to read stream from file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Stream read error", e);
        }
    }
}
