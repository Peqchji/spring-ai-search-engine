package com.search.ai.retrieval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class HybridRetrievalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HybridRetrievalApplication.class, args);
    }
}
