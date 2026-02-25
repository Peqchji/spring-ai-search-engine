package com.search.ai.ingestion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ingestions")
public class IngestionMetadata {
    @Id
    private String id;
    
    private String filename;
    private String contentType;
    private int documentCount;
    private int chunkCount;
    private LocalDateTime ingestedAt;
    private IngestionStatus status;
}
