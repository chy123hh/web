package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "订单响应")
public class OrderResponse {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "接单者ID")
    private Long takerId;

    @Schema(description = "发布者ID")
    private Long publisherId;

    @Schema(description = "悬赏积分")
    private Integer rewardPoints;

    @Schema(description = "状态：1-已接单 2-已完成(待确认) 3-已确认 4-已取消")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "完成凭证图片URL")
    private String completeProofUrl;

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
