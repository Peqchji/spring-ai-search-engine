package com.search.ai.shared.util.constants;

public final class AppConstants {
    private AppConstants() {
    }

    // ── SpEL Bindings for Annotations ──
    // Spring allows us to dynamically resolve .env or application.yml values inside
    // annotations like @Document by using SpEL.
    
    public static final String SPEL_COLLECTION_OUTBOX = "#{@environment.getProperty('app.mongodb.collections.outbox', 'outbox_events')}";
    public static final String SPEL_COLLECTION_INGESTIONS = "#{@environment.getProperty('app.mongodb.collections.ingestions', 'ingestions')}";
    public static final String SPEL_INDEX_LEXICAL = "#{@environment.getProperty('app.elasticsearch.indices.lexical', 'search_documents')}";
}
