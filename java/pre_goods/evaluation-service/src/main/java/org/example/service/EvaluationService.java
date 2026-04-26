package org.example.service;

import org.example.dto.request.EvaluationRequest;
import org.example.dto.response.EvaluationResponse;

import java.util.List;

public interface EvaluationService {

    /**
     * 创建评价
     */
    Long createEvaluation(Long evaluatorId, EvaluationRequest request);

    /**
     * 根据ID获取评价详情
     */
    EvaluationResponse getEvaluationById(Long id);

    /**
     * 查询我收到的评价
     */
    List<EvaluationResponse> getEvaluationsReceived(Long userId);

    /**
     * 查询我发布的评价
     */
    List<EvaluationResponse> getEvaluationsGiven(Long userId);

    /**
     * 查询某个任务的評價
     */
    List<EvaluationResponse> getEvaluationsByTaskId(Long taskId);

    /**
     * 获取用户平均评分
     */
    Double getAverageRating(Long userId);
}
