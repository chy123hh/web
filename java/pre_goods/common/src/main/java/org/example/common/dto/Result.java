package org.example.common.dto;

import lombok.Data;

/**
 * 统一响应结果封装类
 * 所有 API 接口返回结果都使用此类封装
 */
@Data
public class Result<T> {

    /**
     * 响应状态码
     * 200: 成功
     * 100x: 参数错误
     * 200x: 业务错误
     * 300x: 系统错误
     * 500: 服务器错误错误
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Result 对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（不带数据）
     *
     * @param <T> 数据类型
     * @return Result 对象
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    /**
     * 成功响应（自定义消息）
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return Result 对象
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 参数错误响应（1001）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 对象
     */
    public static <T> Result<T> paramError(String message) {
        return error(1001, message);
    }

    /**
     * 业务错误响应（2001）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 对象
     */
    public static <T> Result<T> businessError(String message) {
        return error(2001, message);
    }

    /**
     * 系统错误响应（3001）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result 对象
     */
    public static <T> Result<T> systemError(String message) {
        return error(3001, message);
    }
}