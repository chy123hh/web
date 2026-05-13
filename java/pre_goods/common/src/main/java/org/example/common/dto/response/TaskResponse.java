package org.example.common.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务响应 DTO
 * 用于服务间调用时传递任务信息
 */
@Data
public class TaskResponse {

    /**
     * 任务 ID
     */
    private Long id;

    /**
     * 发布者 ID
     */
    private Long userId;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务类型
     */
    private String type;

    /**
     * 报酬
     */
    private BigDecimal reward;

    /**
     * 取件地点
     */
    private String pickupLocation;

    /**
     * 送达地点
     */
    private String deliveryLocation;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 接单人 ID
     */
    private Long acceptorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;
}
