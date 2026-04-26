package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员视图的用户信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String studentId;
    private String nickname;
    private String avatarUrl;
    private Integer points;
    private Integer creditScore;
    private Integer role;
    private String roleDesc;
    private Integer status;
    private String statusDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 统计信息 */
    private Long publishedTaskCount;
    private Long acceptedTaskCount;
    private Long completedOrderCount;
    private Double averageRating;
}
