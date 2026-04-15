package com.search.ai.orchestrator.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PipelineConstants {

    public static final String REDIS_STATE_KEY_PREFIX = "pipeline:state:";
    
    public static final class States {
        public static final String ENRICHING = "ENRICHING";
        public static final String EXPANDING = "EXPANDING";
        public static final String RETRIEVING = "RETRIEVING";
        public static final String RANKING = "RANKING";
        public static final String DONE = "DONE";
    }

    public static final class Fallbacks {
        public static final int DEFAULT_TOP_K = 20;
    }
}
