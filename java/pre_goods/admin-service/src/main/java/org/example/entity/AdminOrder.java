package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员视图的订单实体
 * 映射 order_service.delivery_order 表（跨库访问）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "delivery_order", schema = "order_service")
public class AdminOrder {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("task_id")
    private Long taskId;

    @TableField("taker_id")
    private Long takerId;

    @TableField("publisher_id")
    private Long publisherId;

    @TableField("reward_points")
    private Integer rewardPoints;

    @TableField("status")
    private Integer status;

    @TableField("complete_proof_url")
    private String completeProofUrl;

    @TableField("confirm_time")
    private LocalDateTime confirmTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    /** 状态常量 */
    public static final Integer STATUS_TAKEN = 1;
    public static final Integer STATUS_COMPLETED = 2;
    public static final Integer STATUS_CONFIRMED = 3;
    public static final Integer STATUS_CANCELLED = 4;
}
