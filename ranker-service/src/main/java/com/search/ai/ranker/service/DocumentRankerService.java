package com.search.ai.ranker.service;

import com.search.ai.shared.model.DocumentCandidate;
import com.search.ai.shared.model.RankedDocument;
import com.search.ai.shared.model.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentRankerService {

    public List<RankedDocument> rank(String query, List<DocumentCandidate> candidates, UserContext userContext) {
        log.info("Ranking {} candidates for query: {}", candidates.size(), query);
        
        // TODO: Implement actual LTR (Learn to Rank) model scoring.
        // For now, we simulate ranking by just returning the candidates with a slight modifier
        
        return candidates.stream()
                .limit(5)
                .map(c -> new RankedDocument(
                        c.id(), 
                        "Simulated summary for document " + c.id(), 
                        Map.of("source", c.source()), 
                        c.score() * 1.1 // Dummy score adjustment
                ))
                .collect(Collectors.toList());
    }
}
