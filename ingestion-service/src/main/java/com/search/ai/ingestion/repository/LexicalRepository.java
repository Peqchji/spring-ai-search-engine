package com.search.ai.ingestion.repository;

import com.search.ai.ingestion.model.LexicalDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LexicalRepository extends ElasticsearchRepository<LexicalDocument, String> {
}
