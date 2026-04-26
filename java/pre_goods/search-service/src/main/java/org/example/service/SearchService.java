package org.example.service;

import org.example.common.dto.Result;

public interface SearchService {

    Result searchTasks(String keyword, String type, Double minReward, Double maxReward, String status, Integer page, Integer size);
}