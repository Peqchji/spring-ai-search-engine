package com.search.ai.ingestion.service;

import com.search.ai.shared.util.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChunkingService {

    private final TokenTextSplitter splitter;

    public ChunkingService(
            @Value("${app.chunking.size:" + AppConstants.DEFAULT_CHUNK_SIZE + "}") int chunkSize,
            @Value("${app.chunking.overlap:" + AppConstants.DEFAULT_CHUNK_OVERLAP + "}") int overlap) {
        log.info("Initializing ChunkingService with size: {} and overlap: {}", chunkSize, overlap);
        this.splitter = new TokenTextSplitter(chunkSize, overlap, 1, 10000, true);
    }

    public List<Document> chunk(List<Document> documents) {
        log.debug("Chunking {} document(s)", documents.size());
        return splitter.apply(documents);
    }
}
