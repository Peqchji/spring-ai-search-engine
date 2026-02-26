package com.search.ai.gateway.controller;

import com.search.ai.shared.constant.APIMessages;
import com.search.ai.shared.model.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public ResponseEntity<ApiResponse<Void>> fallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(APIMessages.SERVICE_UNAVAILABLE));
    }
}
