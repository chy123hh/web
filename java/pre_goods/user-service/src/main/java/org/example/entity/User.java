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
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学号（登录账号）
     */
    @TableField("student_id")
    private String studentId;

    /**
     * 密码（BCrypt加密）
     */
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 积分余额
     */
    @TableField("points")
    private Integer points;

    /**
     * 信用分
     */
    @TableField("credit_score")
    private Integer creditScore;

    /**
     * 角色（0普通用户 1管理员）
     */
    @TableField("role")
    private Integer role;

    /**
     * 状态（1正常 2封禁）
     */
    @TableField("status")
    private Integer status;

    /**
     * 注册时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 角色常量
     */
    public static final Integer ROLE_NORMAL = 0;
    public static final Integer ROLE_ADMIN = 1;

    /**
     * 状态常量
     */
    public static final Integer STATUS_NORMAL = 1;
    public static final Integer STATUS_BANNED = 2;
}