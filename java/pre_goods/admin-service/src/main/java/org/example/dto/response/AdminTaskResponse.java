package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员视图的任务信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTaskResponse {

    private Long id;
    private Long userId;
    private String publisherNickname;
    private String title;
    private String description;
    private String type;
    private BigDecimal reward;
    private String pickupLocation;
    private String deliveryLocation;
    private String status;
    private Long acceptorId;
    private String acceptorNickname;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deadline;
    private Long orderId;
    private String orderNo;
}
