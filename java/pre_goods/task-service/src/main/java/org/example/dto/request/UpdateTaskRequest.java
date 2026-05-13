package org.example.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateTaskRequest {

    private String title;

    private String description;

    private String type;

    private BigDecimal reward;

    private String pickupLocation;

    private String deliveryLocation;

    private LocalDateTime deadline;
}
