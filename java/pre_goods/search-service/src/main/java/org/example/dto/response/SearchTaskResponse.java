package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchTaskResponse {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private String type;

    private BigDecimal reward;

    private String pickupLocation;

    private String deliveryLocation;

    private String status;

    private LocalDateTime deadline;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<String> highlightFields;
}