package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Task;
import org.example.entity.TaskDocument;
import org.example.mapper.TaskMapper;
import org.example.repository.TaskDocumentRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskIndexService {

    private final TaskMapper taskMapper;
    private final TaskDocumentRepository taskDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 全量同步：将所有任务数据从 MySQL 同步到 Elasticsearch
     */
    public void fullSync() {
        log.info("开始全量同步任务数据到 Elasticsearch...");
        long start = System.currentTimeMillis();

        // 先删除旧索引再重建
        IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of("task_index"));
        if (indexOps.exists()) {
            indexOps.delete();
            log.info("已删除旧索引 task_index");
        }
        indexOps.create();
        indexOps.putMapping(indexOps.createMapping(TaskDocument.class));
        log.info("索引 task_index 创建成功");

        // 查询 MySQL 所有任务
        List<Task> tasks = taskMapper.selectList(null);
        if (tasks == null || tasks.isEmpty()) {
            log.info("MySQL 中无任务数据，同步结束");
            return;
        }

        List<TaskDocument> documents = tasks.stream()
                .map(this::convertToDocument)
                .collect(Collectors.toList());

        taskDocumentRepository.saveAll(documents);

        long cost = System.currentTimeMillis() - start;
        log.info("全量同步完成，共同步 {} 条数据，耗时 {} ms", documents.size(), cost);
    }

    private TaskDocument convertToDocument(Task task) {
        return TaskDocument.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .description(task.getDescription())
                .type(task.getType())
                .reward(task.getReward())
                .pickupLocation(task.getPickupLocation())
                .deliveryLocation(task.getDeliveryLocation())
                .status(task.getStatus())
                .acceptorId(task.getAcceptorId())
                .createTime(task.getCreateTime())
                .updateTime(task.getUpdateTime())
                .deadline(task.getDeadline())
                .build();
    }
}
