package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员视图的任务实体
 * 映射 task_service.task 表（跨库访问）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "task", schema = "task_service")
public class AdminTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("type")
    private String type;

    @TableField("reward")
    private BigDecimal reward;

    @TableField("pickup_location")
    private String pickupLocation;

    @TableField("delivery_location")
    private String deliveryLocation;

    @TableField("status")
    private String status;

    @TableField("acceptor_id")
    private Long acceptorId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("deadline")
    private LocalDateTime deadline;

    /** 状态常量 */
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    /** 类型常量 */
    public static final String TYPE_DELIVERY = "DELIVERY";
    public static final String TYPE_PURCHASE = "PURCHASE";
    public static final String TYPE_OTHER = "OTHER";
}
