package com.search.ai.orchestrator.service;

import com.search.ai.shared.model.UserContext;
import org.springframework.stereotype.Component;

@Component
public class UserContextEnricher {

    public UserContext enrich(UserContext partial) {
        // In a real system, this would fetch from Redis/DB
        // For now, we return as-is or add mock data
        if (partial.userId() != null && partial.preferences() == null) {
            // Mock enrichment
            return new UserContext(
                partial.userId(),
                partial.location(),
                java.util.List.of("electronics", "tech"),
                java.util.List.of("laptop return", "warranty")
            );
        }
        return partial;
    }
}
