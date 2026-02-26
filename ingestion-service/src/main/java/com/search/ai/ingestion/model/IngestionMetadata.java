package com.search.ai.ingestion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.search.ai.shared.util.constants.AppConstants;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = AppConstants.SPEL_COLLECTION_INGESTIONS)
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
