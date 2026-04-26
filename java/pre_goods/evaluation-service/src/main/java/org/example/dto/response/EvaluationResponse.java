package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {

    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 评价人ID
     */
    private Long evaluatorId;

    /**
     * 被评价人ID
     */
    private Long evaluatedId;

    /**
     * 评分（1-5星）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价类型：1-对接单人评价，2-对发布者评价
     */
    private Integer type;

    /**
     * 评价类型描述
     */
    private String typeDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
