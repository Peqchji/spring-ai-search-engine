package com.search.ai.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SearchOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchOrchestratorApplication.class, args);
    }
}
