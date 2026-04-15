package com.search.ai.orchestrator.config;

import com.search.ai.shared.constant.TopicConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("pipelineProperties")
@ConfigurationProperties(prefix = "search.pipeline")
@Data
public class PipelineProperties {

    /**
     * Timeout for each stage of the search pipeline.
     */
    private long timeoutMs = 30000;

    /**
     * Kafka Topic Names map.
     */
    private Topics topics = new Topics();

    @Data
    public static class Topics {
        private String queryExpand = TopicConstants.QUERY_EXPAND;
        private String queryExpanded = TopicConstants.QUERY_EXPANDED;
        private String retrievalRequest = TopicConstants.RETRIEVAL_REQUEST;
        private String retrievalResults = TopicConstants.RETRIEVAL_RESULTS;
        private String rankRequest = TopicConstants.RANK_REQUEST;
        private String rankResults = TopicConstants.RANK_RESULTS;
    }
}
