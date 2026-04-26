package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.TaskIndexService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskIndexInitializer implements CommandLineRunner {

    private final TaskIndexService taskIndexService;

    @Override
    public void run(String... args) {
        log.info("应用启动，开始初始化 Elasticsearch 任务索引...");
        try {
            taskIndexService.fullSync();
        } catch (Exception e) {
            log.error("Elasticsearch 索引初始化失败", e);
        }
    }
}
