package com.search.ai.orchestrator.controller;

import com.search.ai.orchestrator.service.PipelineOrchestrator;
import com.search.ai.shared.model.SearchRequest;
import com.search.ai.shared.model.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final PipelineOrchestrator orchestrator;

    @PostMapping
    public CompletableFuture<SearchResponse> search(@RequestBody SearchRequest request) {
        return orchestrator.search(request.query(), request.userContext());
    }
}
