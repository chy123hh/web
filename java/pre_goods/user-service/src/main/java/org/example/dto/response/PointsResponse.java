package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 积分响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 积分余额
     */
    private Integer points;
}