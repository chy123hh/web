package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @GetMapping("/tasks")
  public Result searchTasks(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Double minReward,
      @RequestParam(required = false) Double maxReward,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer size) {
    return searchService.searchTasks(keyword, type, minReward, maxReward, status, page, size);
  }
}
