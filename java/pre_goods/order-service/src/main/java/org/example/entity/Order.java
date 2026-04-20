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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("delivery_order")
public class Order {

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

    // 状态常量
    public static final Integer STATUS_TAKEN = 1;      // 已接单
    public static final Integer STATUS_COMPLETED = 2;  // 已完成（待确认）
    public static final Integer STATUS_CONFIRMED = 3;  // 已确认
    public static final Integer STATUS_CANCELLED = 4;  // 已取消
}
