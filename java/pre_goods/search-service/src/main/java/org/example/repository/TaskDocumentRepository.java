package org.example.repository;

import org.example.entity.TaskDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDocumentRepository extends ElasticsearchRepository<TaskDocument, Long> {
}
