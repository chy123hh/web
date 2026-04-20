package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Integer type;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime readTime;

    public static final Integer TYPE_SYSTEM = 1;
    public static final Integer TYPE_USER = 2;

    public static final Integer STATUS_UNREAD = 0;
    public static final Integer STATUS_READ = 1;
}
