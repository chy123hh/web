package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户登录请求 DTO
 */
@Data
public class LoginRequest {

    /**
     * 学号（登录账号）
     */
    @NotBlank(message = "学号不能为空")
    @Size(min = 3, max = 20, message = "学号长度必须在3-20之间")
    private String studentId;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50之间")
    private String password;
}