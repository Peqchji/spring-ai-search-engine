package com.search.ai.shared.constant;

public final class APIMessages {

    private APIMessages() {
    }

    // ── Gateway ──
    public static final String SERVICE_UNAVAILABLE = "Service is temporarily unavailable. Please try again later.";

    // ── Ingestion ──
    public static final String INGEST_SUCCESS = "File ingested successfully";
    public static final String INGEST_ASYNC_STARTED = "Ingestion job started in the background.";
    public static final String ERROR_ASYNC_IO = "Async I/O Error";
    public static final String ERROR_METADATA_NOT_FOUND = "IngestionMetadata not found for ID: ";
}
