package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户资料响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 积分余额
     */
    private Integer points;

    /**
     * 信用分
     */
    private Integer creditScore;

    /**
     * 角色（0普通用户 1管理员）
     */
    private Integer role;

    /**
     * 状态（1正常 2封禁）
     */
    private Integer status;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;
}