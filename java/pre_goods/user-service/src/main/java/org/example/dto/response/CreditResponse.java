package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 信用分响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 信用分
     */
    private Integer creditScore;

    /**
     * 是否可以接单（信用分 >= 60）
     */
    private Boolean canTakeOrder;
}