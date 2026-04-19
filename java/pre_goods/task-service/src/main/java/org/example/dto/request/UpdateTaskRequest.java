package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "更新任务请求")
public class UpdateTaskRequest {

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

    @Schema(description = "截止时间")
    private LocalDateTime deadline;
}
