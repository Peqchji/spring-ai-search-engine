package com.search.ai.expansion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class QueryExpansionApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryExpansionApplication.class, args);
    }
}
