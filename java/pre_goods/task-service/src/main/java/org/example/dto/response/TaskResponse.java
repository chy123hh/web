package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "任务响应")
public class TaskResponse {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "发布用户ID")
    private Long userId;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "任务类型")
    private String type;

    @Schema(description = "任务报酬")
    private BigDecimal reward;

    @Schema(description = "取货地点")
    private String pickupLocation;

    @Schema(description = "送货地点")
    private String deliveryLocation;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "接单人ID")
    private Long acceptorId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;
}
