package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "搜索服务", description = "任务搜索接口")
public class SearchController {

  private final SearchService searchService;

  @GetMapping("/tasks")
  @Operation(summary = "全文搜索任务", description = "根据关键词、类型、奖励范围等条件搜索任务")
  public Result searchTasks(
      @Parameter(description = "关键词（搜索标题和描述）") @RequestParam(required = false) String keyword,
      @Parameter(description = "任务类型：DELIVERY-快递代取, PURCHASE-代买餐饮, OTHER-其他") @RequestParam(required = false) String type,
      @Parameter(description = "最低奖励金额") @RequestParam(required = false) Double minReward,
      @Parameter(description = "最高奖励金额") @RequestParam(required = false) Double maxReward,
      @Parameter(description = "任务状态：PENDING-待接单, ACCEPTED-已接单, COMPLETED-已完成, CANCELLED-已取消") @RequestParam(required = false) String status,
      @Parameter(description = "页码（默认1）") @RequestParam(defaultValue = "1") Integer page,
      @Parameter(description = "每页数量（默认10）") @RequestParam(defaultValue = "10") Integer size) {
    return searchService.searchTasks(keyword, type, minReward, maxReward, status, page, size);
  }
}