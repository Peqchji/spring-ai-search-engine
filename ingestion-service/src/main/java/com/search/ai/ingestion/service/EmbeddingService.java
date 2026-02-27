package com.search.ai.ingestion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final VectorStore vectorStore;

    /**
     * Generates embeddings and stores them in MongoDB using Spring AI VectorStore.
     * The CDC connector will then pick up these writes and sync them to
     * Elasticsearch.
     */
    public void embedAndStore(List<Document> chunks) {
        log.info("Generating embeddings and storing {} chunks in MongoDB via Spring AI...", chunks.size());

        // This handles embedding (via the configured EmbeddingModel) and persistence
        // automatically
        vectorStore.add(chunks);

        log.info("Successfully pushed {} chunks to MongoDB", chunks.size());
    }
}
