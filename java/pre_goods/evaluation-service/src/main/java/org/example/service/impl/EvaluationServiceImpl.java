package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.exception.BusinessException;
import org.example.dto.request.EvaluationRequest;
import org.example.dto.response.EvaluationResponse;
import org.example.entity.Evaluation;
import org.example.mapper.EvaluationMapper;
import org.example.service.EvaluationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationMapper evaluationMapper;

    @Override
    public Long createEvaluation(Long evaluatorId, EvaluationRequest request) {
        // 验证评价类型
        if (!Evaluation.TYPE_TO_TAKER.equals(request.getType()) 
            && !Evaluation.TYPE_TO_PUBLISHER.equals(request.getType())) {
            throw new BusinessException(400, "评价类型不正确");
        }

        // 检查是否已经评价过
        LambdaQueryWrapper<Evaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Evaluation::getTaskId, request.getTaskId())
                   .eq(Evaluation::getOrderId, request.getOrderId())
                   .eq(Evaluation::getEvaluatorId, evaluatorId)
                   .eq(Evaluation::getType, request.getType())
                   .eq(Evaluation::getDeleted, 0);
        
        Long count = evaluationMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(400, "您已经评价过该订单");
        }

        // 创建评价
        Evaluation evaluation = Evaluation.builder()
                .taskId(request.getTaskId())
                .orderId(request.getOrderId())
                .evaluatorId(evaluatorId)
                .evaluatedId(request.getEvaluatedId())
                .rating(request.getRating())
                .content(request.getContent())
                .type(request.getType())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .deleted(0)
                .build();

        evaluationMapper.insert(evaluation);
        log.info("创建评价成功，评价ID: {}, 评价人: {}, 被评价人: {}", 
                evaluation.getId(), evaluatorId, request.getEvaluatedId());
        
        return evaluation.getId();
    }

    @Override
    public EvaluationResponse getEvaluationById(Long id) {
        Evaluation evaluation = evaluationMapper.selectById(id);
        if (evaluation == null || evaluation.getDeleted() == 1) {
            throw new BusinessException(404, "评价不存在");
        }
        return convertToResponse(evaluation);
    }

    @Override
    public List<EvaluationResponse> getEvaluationsReceived(Long userId) {
        LambdaQueryWrapper<Evaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Evaluation::getEvaluatedId, userId)
                   .eq(Evaluation::getDeleted, 0)
                   .orderByDesc(Evaluation::getCreateTime);
        
        List<Evaluation> evaluations = evaluationMapper.selectList(queryWrapper);
        return evaluations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluationResponse> getEvaluationsGiven(Long userId) {
        LambdaQueryWrapper<Evaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Evaluation::getEvaluatorId, userId)
                   .eq(Evaluation::getDeleted, 0)
                   .orderByDesc(Evaluation::getCreateTime);
        
        List<Evaluation> evaluations = evaluationMapper.selectList(queryWrapper);
        return evaluations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluationResponse> getEvaluationsByTaskId(Long taskId) {
        LambdaQueryWrapper<Evaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Evaluation::getTaskId, taskId)
                   .eq(Evaluation::getDeleted, 0)
                   .orderByDesc(Evaluation::getCreateTime);
        
        List<Evaluation> evaluations = evaluationMapper.selectList(queryWrapper);
        return evaluations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRating(Long userId) {
        LambdaQueryWrapper<Evaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Evaluation::getEvaluatedId, userId)
                   .eq(Evaluation::getDeleted, 0)
                   .select(Evaluation::getRating);
        
        List<Evaluation> evaluations = evaluationMapper.selectList(queryWrapper);
        if (evaluations.isEmpty()) {
            return 0.0;
        }
        
        double sum = evaluations.stream()
                .mapToInt(Evaluation::getRating)
                .sum();
        
        return sum / evaluations.size();
    }

    private EvaluationResponse convertToResponse(Evaluation evaluation) {
        String typeDesc = Evaluation.TYPE_TO_TAKER.equals(evaluation.getType()) 
                ? "对接单人评价" 
                : "对发布者评价";
        
        return EvaluationResponse.builder()
                .id(evaluation.getId())
                .taskId(evaluation.getTaskId())
                .orderId(evaluation.getOrderId())
                .evaluatorId(evaluation.getEvaluatorId())
                .evaluatedId(evaluation.getEvaluatedId())
                .rating(evaluation.getRating())
                .content(evaluation.getContent())
                .type(evaluation.getType())
                .typeDesc(typeDesc)
                .createTime(evaluation.getCreateTime())
                .build();
    }
}
