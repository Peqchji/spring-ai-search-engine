package com.search.ai.ingestion.repository;

import com.search.ai.ingestion.model.IngestionMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestionMetadataRepository extends MongoRepository<IngestionMetadata, String> {
}
