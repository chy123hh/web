package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 * 使用 BCrypt 强哈希算法进行密码加密和验证
 * 
 * 企业级最佳实践：
 * 1. BCrypt 是自适应的单向函数，安全性高
 * 2. 自动处理盐值（Salt），防止彩虹表攻击
 * 3. 可配置加密强度（默认 10 轮）
 */
@Slf4j
@Component
public class PasswordUtil {

    /**
     * PasswordEncoder 实例
     * BCryptPasswordEncoder 使用 BCrypt 算法
     */
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密密码
     * 使用 BCrypt 算法对原始密码进行加密
     * 
     * @param rawPassword 原始密码（明文）
     * @return 加密后的密码（密文）
     */
    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            log.error("密码不能为空");
            throw new IllegalArgumentException("密码不能为空");
        }
        String encoded = PASSWORD_ENCODER.encode(rawPassword);
        log.debug("密码加密完成");
        return encoded;
    }

    /**
     * 验证密码
     * 将原始密码与加密后的密码进行比对
     * 
     * @param rawPassword 原始密码（明文）
     * @param encodedPassword 加密后的密码（密文）
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            log.error("原始密码不能为空");
            return false;
        }
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            log.error("加密密码不能为空");
            return false;
        }
        boolean result = PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
        log.debug("密码验证完成，结果：{}", result);
        return result;
    }

    /**
     * 判断密码是否需要升级（可选）
     * BCrypt 会自动处理，此方法通常返回 false
     * 
     * @param encodedPassword 加密后的密码
     * @return 是否需要升级
     */
    public boolean upgradeEncoding(String encodedPassword) {
        return PASSWORD_ENCODER.upgradeEncoding(encodedPassword);
    }
}
