package org.example.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaskResponse {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private String type;

    private BigDecimal reward;

    private String pickupLocation;

    private String deliveryLocation;

    private String status;

    private Long acceptorId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime deadline;
}
