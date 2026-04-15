package com.search.ai.shared.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TopicConstants {

    public static final String QUERY_EXPAND = "query.expand";
    public static final String QUERY_EXPANDED = "query.expanded";
    
    public static final String RETRIEVAL_REQUEST = "retrieval.request";
    public static final String RETRIEVAL_RESULTS = "retrieval.results";
    
    public static final String RANK_REQUEST = "rank.request";
    public static final String RANK_RESULTS = "rank.results";

    // ── Property Placeholders for @KafkaListener (No Magic Strings) ──
    public static final String PROP_TOPIC_QUERY_EXPANDED = "${search.pipeline.topics.query-expanded:" + QUERY_EXPANDED + "}";
    public static final String PROP_TOPIC_RETRIEVAL_RESULTS = "${search.pipeline.topics.retrieval-results:" + RETRIEVAL_RESULTS + "}";
    public static final String PROP_TOPIC_RANK_RESULTS = "${search.pipeline.topics.rank-results:" + RANK_RESULTS + "}";

}
