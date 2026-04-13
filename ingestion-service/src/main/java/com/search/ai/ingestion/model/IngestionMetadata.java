package com.search.ai.ingestion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import com.search.ai.shared.util.constants.AppConstants;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = AppConstants.SPEL_INDEX_INGESTION_METADATA)
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
