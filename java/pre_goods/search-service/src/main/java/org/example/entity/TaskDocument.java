package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "task_index")
public class TaskDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long, name = "user_id")
    private Long userId;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Keyword, name = "type")
    private String type;

    @Field(type = FieldType.Double, name = "reward")
    private BigDecimal reward;

    @Field(type = FieldType.Keyword, name = "pickup_location")
    private String pickupLocation;

    @Field(type = FieldType.Keyword, name = "delivery_location")
    private String deliveryLocation;

    @Field(type = FieldType.Keyword, name = "status")
    private String status;

    @Field(type = FieldType.Long, name = "acceptor_id")
    private Long acceptorId;

    @Field(type = FieldType.Date, name = "create_time", format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @Field(type = FieldType.Date, name = "update_time", format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;

    @Field(type = FieldType.Date, name = "deadline", format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;
}
