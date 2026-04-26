package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Result;
import org.example.common.util.JwtUtil;
import org.example.dto.request.EvaluationRequest;
import org.example.dto.response.EvaluationResponse;
import org.example.service.EvaluationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final JwtUtil jwtUtil;

    /**
     * 创建评价
     */
    @PostMapping
    public Result<Long> createEvaluation(@Valid @RequestBody EvaluationRequest request,
                                         HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        Long evaluatorId = jwtUtil.getUserIdFromToken(token);
        
        Long evaluationId = evaluationService.createEvaluation(evaluatorId, request);
        return Result.success(evaluationId);
    }

    /**
     * 获取评价详情
     */
    @GetMapping("/{id}")
    public Result<EvaluationResponse> getEvaluation(@PathVariable Long id) {
        EvaluationResponse evaluation = evaluationService.getEvaluationById(id);
        return Result.success(evaluation);
    }

    /**
     * 查询我收到的评价
     */
    @GetMapping("/received")
    public Result<List<EvaluationResponse>> getReceivedEvaluations(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        List<EvaluationResponse> evaluations = evaluationService.getEvaluationsReceived(userId);
        return Result.success(evaluations);
    }

    /**
     * 查询我发布的评价
     */
    @GetMapping("/given")
    public Result<List<EvaluationResponse>> getGivenEvaluations(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        List<EvaluationResponse> evaluations = evaluationService.getEvaluationsGiven(userId);
        return Result.success(evaluations);
    }

    /**
     * 查询某个任务的评价
     */
    @GetMapping("/task/{taskId}")
    public Result<List<EvaluationResponse>> getTaskEvaluations(@PathVariable Long taskId) {
        List<EvaluationResponse> evaluations = evaluationService.getEvaluationsByTaskId(taskId);
        return Result.success(evaluations);
    }

    /**
     * 获取用户平均评分
     */
    @GetMapping("/average-rating/{userId}")
    public Result<Double> getAverageRating(@PathVariable Long userId) {
        Double averageRating = evaluationService.getAverageRating(userId);
        return Result.success(averageRating);
    }
}
