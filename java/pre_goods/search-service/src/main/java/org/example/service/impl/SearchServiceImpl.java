package org.example.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.Result;
import org.example.dto.response.SearchTaskResponse;
import org.example.entity.Task;
import org.example.entity.TaskDocument;
import org.example.mapper.TaskMapper;
import org.example.service.SearchService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

  private final ElasticsearchOperations elasticsearchOperations;
  private final TaskMapper taskMapper;

  @Override
  public Result searchTasks(String keyword, String type, Double minReward, Double maxReward, String status,
      Integer page, Integer size) {
    log.info("全文搜索任务 - keyword: {}, type: {}, minReward: {}, maxReward: {}, status: {}, page: {}, size: {}",
        keyword, type, minReward, maxReward, status, page, size);

    int currentPage = page != null && page > 0 ? page : 1;
    int pageSize = size != null && size > 0 ? size : 10;

    try {
      return searchByElasticsearch(keyword, type, minReward, maxReward, status, currentPage, pageSize);
    } catch (Exception e) {
      log.warn("Elasticsearch 查询失败，回退到 MySQL 查询: {}", e.getMessage());
      return searchByMySql(keyword, type, minReward, maxReward, status, currentPage, pageSize);
    }
  }

  private Result searchByElasticsearch(String keyword, String type, Double minReward, Double maxReward,
      String status, int currentPage, int pageSize) {
    Criteria criteria = new Criteria();

    if (keyword != null && !keyword.isEmpty()) {
      Criteria orCriteria = new Criteria("title").matches(keyword)
          .or(new Criteria("description").matches(keyword));
      criteria.and(orCriteria);
    }

    if (type != null && !type.isEmpty()) {
      criteria.and("type").is(type);
    }

    if (minReward != null) {
      criteria.and("reward").greaterThanEqual(minReward);
    }

    if (maxReward != null) {
      criteria.and("reward").lessThanEqual(maxReward);
    }

    if (status != null && !status.isEmpty()) {
      criteria.and("status").is(status);
    } else {
      criteria.and("status").is("PENDING");
    }

    CriteriaQuery query = new CriteriaQuery(criteria);
    query.setPageable(PageRequest.of(currentPage - 1, pageSize, Sort.by(Sort.Direction.DESC, "create_time")));

    HighlightParameters highlightParameters = HighlightParameters.builder()
        .withPreTags("<em>")
        .withPostTags("</em>")
        .build();
    List<HighlightField> highlightFields = Arrays.asList(
        new HighlightField("title"),
        new HighlightField("description")
    );
    Highlight highlight = new Highlight(highlightParameters, highlightFields);
    query.setHighlightQuery(new HighlightQuery(highlight, TaskDocument.class));

    SearchHits<TaskDocument> searchHits = elasticsearchOperations.search(query, TaskDocument.class);

    List<SearchTaskResponse> responseList = searchHits.getSearchHits().stream()
        .map(hit -> convertEsHitToResponse(hit))
        .collect(Collectors.toList());

    long total = searchHits.getTotalHits();
    long pages = (total + pageSize - 1) / pageSize;

    return Result.success(new PageResult(total, pages, (long) currentPage, (long) pageSize, responseList));
  }

  private Result searchByMySql(String keyword, String type, Double minReward, Double maxReward,
      String status, int currentPage, int pageSize) {
    Page<Task> pageRequest = new Page<>(currentPage, pageSize);
    IPage<Task> taskPage = taskMapper.searchTasks(pageRequest, keyword, type, minReward, maxReward, status);

    List<SearchTaskResponse> responseList = taskPage.getRecords().stream()
        .map(task -> convertTaskToResponse(task, keyword))
        .collect(Collectors.toList());

    return Result.success(new PageResult(taskPage.getTotal(), taskPage.getPages(), taskPage.getCurrent(),
        taskPage.getSize(), responseList));
  }

  private SearchTaskResponse convertEsHitToResponse(SearchHit<TaskDocument> hit) {
    TaskDocument doc = hit.getContent();
    List<String> highlightFields = new ArrayList<>();

    Map<String, List<String>> highlightMap = hit.getHighlightFields();
    if (highlightMap != null) {
      if (highlightMap.containsKey("title")) {
        highlightFields.add("title");
        doc.setTitle(String.join(" ", highlightMap.get("title")));
      }
      if (highlightMap.containsKey("description")) {
        highlightFields.add("description");
        doc.setDescription(String.join(" ", highlightMap.get("description")));
      }
    }

    return SearchTaskResponse.builder()
        .id(doc.getId())
        .userId(doc.getUserId())
        .title(doc.getTitle())
        .description(doc.getDescription())
        .type(doc.getType())
        .reward(doc.getReward())
        .pickupLocation(doc.getPickupLocation())
        .deliveryLocation(doc.getDeliveryLocation())
        .status(doc.getStatus())
        .deadline(doc.getDeadline())
        .createTime(doc.getCreateTime())
        .updateTime(doc.getUpdateTime())
        .highlightFields(highlightFields)
        .build();
  }

  private SearchTaskResponse convertTaskToResponse(Task task, String keyword) {
    List<String> highlightFields = new ArrayList<>();

    if (keyword != null && !keyword.isEmpty()) {
      String lowerKeyword = keyword.toLowerCase();
      if (task.getTitle() != null && task.getTitle().toLowerCase().contains(lowerKeyword)) {
        highlightFields.add("title");
      }
      if (task.getDescription() != null && task.getDescription().toLowerCase().contains(lowerKeyword)) {
        highlightFields.add("description");
      }
    }

    return SearchTaskResponse.builder()
        .id(task.getId())
        .userId(task.getUserId())
        .title(task.getTitle())
        .description(task.getDescription())
        .type(task.getType())
        .reward(task.getReward())
        .pickupLocation(task.getPickupLocation())
        .deliveryLocation(task.getDeliveryLocation())
        .status(task.getStatus())
        .deadline(task.getDeadline())
        .createTime(task.getCreateTime())
        .updateTime(task.getUpdateTime())
        .highlightFields(highlightFields)
        .build();
  }

  @lombok.Data
  @lombok.AllArgsConstructor
  public static class PageResult {
    private Long total;
    private Long pages;
    private Long current;
    private Long size;
    private List<SearchTaskResponse> records;
  }
}