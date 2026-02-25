package com.search.ai.ingestion.repository;

import com.search.ai.ingestion.model.OutboxEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OutboxRepository extends MongoRepository<OutboxEvent, String> {
}
