package com.search.ai.ingestion.repository;

import com.search.ai.ingestion.model.IngestionMetadata;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestionMetadataRepository extends ElasticsearchRepository<IngestionMetadata, String> {
}
