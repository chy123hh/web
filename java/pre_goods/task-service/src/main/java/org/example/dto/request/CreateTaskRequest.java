package org.example.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "创建任务请求")
public class CreateTaskRequest {

    @Schema(description = "任务标题", required = true)
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "任务类型：DELIVERY-代取快递, PURCHASE-代买, OTHER-其他", required = true)
    private String type;

    @Schema(description = "任务报酬", required = true)
    private BigDecimal reward;

    @Schema(description = "取货地点")
    private String pickupLocation;

    @Schema(description = "送货地点", required = true)
    private String deliveryLocation;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;
}
