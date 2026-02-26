package com.search.ai.ingestion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.mongodb.lang.NonNull;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final VectorStore vectorStore;

    /**
     * Embeds and stores documents in the vector store (MongoDB).   
     * Spring AI's VectorStore automatically handles embedding generati on
     * via the configured EmbeddingModel (Ollama) before storing.
     */
    public void embedAndStore(@NonNull List<Document> chunks) {
        vectorStore.add(chunks);

        log.info("Successfully embedded and stored {} chunks in vector store", chunks.size());
    }
}
