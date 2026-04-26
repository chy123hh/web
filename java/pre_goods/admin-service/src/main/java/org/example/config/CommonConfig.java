package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 组件扫描配置
 * 扫描 common 模块中的工具类
 */
@Configuration
@ComponentScan(basePackages = {"org.example.common"})
public class CommonConfig {
}
