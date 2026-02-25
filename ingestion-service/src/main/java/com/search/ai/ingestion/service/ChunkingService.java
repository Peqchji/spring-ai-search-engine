package com.search.ai.ingestion.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChunkingService {

    private final TokenTextSplitter splitter;

    public ChunkingService() {
        this.splitter = new TokenTextSplitter();
    }

    public List<Document> chunk(List<Document> documents) {
        return splitter.apply(documents);
    }
}
