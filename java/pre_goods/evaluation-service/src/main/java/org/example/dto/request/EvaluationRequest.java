package org.example.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluationRequest {

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 被评价人ID（接单人）
     */
    @NotNull(message = "被评价人ID不能为空")
    private Long evaluatedId;

    /**
     * 评分（1-5星）
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1星")
    @Max(value = 5, message = "评分最高为5星")
    private Integer rating;

    /**
     * 评价内容
     */
    @NotBlank(message = "评价内容不能为空")
    private String content;

    /**
     * 评价类型：1-对接单人评价，2-对发布者评价
     */
    @NotNull(message = "评价类型不能为空")
    private Integer type;
}
