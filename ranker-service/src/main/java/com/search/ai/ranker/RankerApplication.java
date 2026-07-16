package com.search.ai.ranker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class RankerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RankerApplication.class, args);
    }
}
