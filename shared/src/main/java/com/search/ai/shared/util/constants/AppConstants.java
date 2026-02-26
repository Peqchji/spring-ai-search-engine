package com.search.ai.shared.util.constants;

public final class AppConstants {
    private AppConstants() {
    }

    // ── SpEL Bindings for @Document and @Document Annotations ──
    public static final String SPEL_COLLECTION_OUTBOX = "#{@environment.getProperty('app.mongodb.collections.outbox', 'outbox_events')}";
    public static final String SPEL_COLLECTION_INGESTIONS = "#{@environment.getProperty('app.mongodb.collections.ingestions', 'ingestions')}";
    public static final String SPEL_INDEX_LEXICAL = "#{@environment.getProperty('app.elasticsearch.indices.lexical', 'search_documents')}";
    public static final String PROP_KAFKA_TOPIC_RAW_DOCS = "${app.kafka.topics.raw-docs:raw-docs}";
    public static final String PROP_KAFKA_CONNECT_URL = "${kafka.connect.url:http://localhost:8083}";
    public static final String RESOURCE_ES_SINK_CONNECTOR = "classpath:connectors/elasticsearch-sink.json";
    public static final String RESOURCE_MONGODB_SINK_CONNECTOR = "classpath:connectors/mongodb-sink.json";
    public static final String RESOURCE_MONGODB_SOURCE_CONNECTOR = "classpath:connectors/mongodb-source.json";
    public static final String PROP_EVENT_INGESTION_COMPLETED = "${app.events.ingestion-completed:INGESTION_COMPLETED}";
    public static final String PROP_TEMP_FILE_PREFIX = "${app.file.temp-prefix:async-ingest-}";
    public static final String PROP_TEMP_FILE_RETENTION_DAYS = "${app.cleanup.temp-file-retention-days:1}";
    public static final String PROP_TEMP_FILE_RATE_MS = "${app.cleanup.temp-file-rate-ms:3600000}";
    public static final String PROP_CHUNKING_SIZE = "${app.chunking.size:800}";
    public static final String PROP_CHUNKING_OVERLAP = "${app.chunking.overlap:100}";
    public static final String PROP_COLLECTION_OUTBOX = "${app.mongodb.collections.outbox:outbox_events}";

    // Connector Names
    public static final String PROP_NAME_ES_SINK_CONNECTOR = "${kafka.connect.names.elasticsearch-sink:elasticsearch-sink-connector}";
    public static final String PROP_NAME_MONGODB_SOURCE_CONNECTOR = "${kafka.connect.names.mongodb-source:mongodb-source-connector}";
}
