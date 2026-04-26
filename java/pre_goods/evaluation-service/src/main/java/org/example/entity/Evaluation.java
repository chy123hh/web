package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("evaluation")
public class Evaluation {

    @TableId(type = IdType.AUTO)
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
     * 评价人ID（发布者）
     */
    private Long evaluatorId;

    /**
     * 被评价人ID（接单人）
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除字段
     */
    private Integer deleted;

    public static final Integer TYPE_TO_TAKER = 1;
    public static final Integer TYPE_TO_PUBLISHER = 2;
}
