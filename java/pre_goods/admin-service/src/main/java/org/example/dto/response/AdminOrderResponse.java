package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员视图的订单信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderResponse {

    private Long id;
    private String orderNo;
    private Long taskId;
    private String taskTitle;
    private Long takerId;
    private String takerNickname;
    private Long publisherId;
    private String publisherNickname;
    private Integer rewardPoints;
    private Integer status;
    private String statusDesc;
    private String completeProofUrl;
    private LocalDateTime confirmTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
