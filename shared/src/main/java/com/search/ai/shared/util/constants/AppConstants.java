package com.search.ai.shared.util.constants;

public final class AppConstants {
    private AppConstants() {
    }

    // Event Types
    public static final String EVENT_TYPE_INGESTION_COMPLETED = "INGESTION_COMPLETED";

    // MongoDB Collections
    public static final String COLLECTION_OUTBOX = "outbox_events";
    public static final String COLLECTION_DOCUMENTS = "documents";
    public static final String COLLECTION_METADATA = "ingestions";

    // Elasticsearch Indices
    public static final String INDEX_LEXICAL_DOCUMENTS = "search_documents";

    // Kafka Topics
    public static final String TOPIC_RAW_DOCUMENTS = "raw-docs";

    // Statuses
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_ERROR = "ERROR";

    // File Handling
    public static final String TEMP_FILE_PREFIX_INGEST = "async-ingest-";

    // Chunking Defaults
    public static final int DEFAULT_CHUNK_SIZE = 800;
    public static final int DEFAULT_CHUNK_OVERLAP = 100;
}
