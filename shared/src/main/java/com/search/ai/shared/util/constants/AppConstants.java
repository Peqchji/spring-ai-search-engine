package com.search.ai.shared.util.constants;

public final class AppConstants {
    private AppConstants() {
    }

    // ── SpEL Bindings for Elasticsearch @Document Annotations ──
    public static final String SPEL_INDEX_LEXICAL = "#{@environment.getProperty('app.elasticsearch.indices.lexical', 'search_documents')}";
    public static final String SPEL_INDEX_INGESTION_METADATA = "ingestion_metadata";

    // ── Application Properties ──
    public static final String PROP_TEMP_FILE_PREFIX = "${app.file.temp-prefix:async-ingest-}";
    public static final String PROP_TEMP_FILE_RETENTION_DAYS = "${app.cleanup.temp-file-retention-days:1}";
    public static final String PROP_TEMP_FILE_RATE_MS = "${app.cleanup.temp-file-rate-ms:3600000}";
    public static final String PROP_CHUNKING_SIZE = "${app.chunking.size:800}";
    public static final String PROP_CHUNKING_OVERLAP = "${app.chunking.overlap:100}";
}
