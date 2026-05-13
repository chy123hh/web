package org.example.feign.config;

import feign.okhttp.OkHttpClient;
import okhttp3.ConnectionPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * OkHttp 连接池配置
 * 用于优化 Feign 客户端的 HTTP 连接性能
 */
@Configuration
public class OkHttpConfig {

    @Value("${feign.okhttp.connection-pool.max-idle-connections:5}")
    private int maxIdleConnections;

    @Value("${feign.okhttp.connection-pool.keep-alive-minutes:5}")
    private int keepAliveMinutes;

    /**
     * 配置 OkHttp 连接池
     * 使用 OkHttp 作为 Feign 的 HTTP 客户端，替代默认的 HttpURLConnection
     * 优势：
     * 1. 连接复用：减少 TCP 握手开销
     * 2. 连接池管理：避免频繁创建连接
     * 3. 自动重试：提高请求成功率
     * 4. GZIP 压缩：减少传输数据量
     */
    @Bean
    public OkHttpClient okHttpClient(ConnectionPool connectionPool) {
        okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
        
        // 设置连接池
        builder.connectionPool(connectionPool);
        
        // 设置连接超时
        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        
        // 设置读取超时
        builder.readTimeout(10000, TimeUnit.MILLISECONDS);
        
        // 设置写入超时
        builder.writeTimeout(10000, TimeUnit.MILLISECONDS);
        
        // 启用 GZIP 压缩
        builder.addInterceptor(chain -> {
            okhttp3.Request request = chain.request().newBuilder()
                    .addHeader("Accept-Encoding", "gzip")
                    .build();
            return chain.proceed(request);
        });
        
        return new OkHttpClient(builder.build());
    }

    /**
     * 配置连接池参数
     */
    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(
                maxIdleConnections, // 最大空闲连接数
                keepAliveMinutes, // 连接存活时间（分钟）
                TimeUnit.MINUTES // 时间单位
        );
    }
}
