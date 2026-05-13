package org.example.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponse {

    private Long id;

    private String orderNo;

    private Long taskId;

    private Long takerId;

    private Long publisherId;

    private Integer rewardPoints;

    private Integer status;

    private String statusDesc;

    private String completeProofUrl;

    private LocalDateTime confirmTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
