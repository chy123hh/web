package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理员视图的用户实体
 * 映射 user_service.user 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class AdminUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private String studentId;

    @TableField("password")
    private String password;

    @TableField("nickname")
    private String nickname;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("points")
    private Integer points;

    @TableField("credit_score")
    private Integer creditScore;

    @TableField("role")
    private Integer role;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    /** 角色常量 */
    public static final Integer ROLE_NORMAL = 0;
    public static final Integer ROLE_ADMIN = 1;

    /** 状态常量 */
    public static final Integer STATUS_NORMAL = 1;
    public static final Integer STATUS_BANNED = 2;
}
