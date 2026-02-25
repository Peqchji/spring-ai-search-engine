package com.search.ai.ingestion.controller;

import com.search.ai.ingestion.service.IngestionFacade;
import com.search.ai.ingestion.service.IngestionFacade.IngestionResult;
import com.search.ai.shared.constant.APIMessages;
import com.search.ai.shared.model.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionFacade ingestionFacade;

    @PostMapping("/ingest")
    public ResponseEntity<ApiResponse<IngestionResult>> ingest(@RequestParam("file") MultipartFile file) {
        IngestionResult result = ingestionFacade.ingest(file);

        return ResponseEntity.ok(
                ApiResponse.success(APIMessages.INGEST_SUCCESS, result));
    }
}
