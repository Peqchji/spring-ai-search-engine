package com.search.ai.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@org.springframework.scheduling.annotation.EnableScheduling
@SpringBootApplication
public class IngestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngestionServiceApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.data.mongodb.MongoTransactionManager transactionManager(
            org.springframework.data.mongodb.MongoDatabaseFactory dbFactory) {
        return new org.springframework.data.mongodb.MongoTransactionManager(dbFactory);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.data.mongodb.core.messaging.MessageListenerContainer messageListenerContainer(
            org.springframework.data.mongodb.core.MongoTemplate template) {
        return new org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer(template);
    }
}
