package org.example.common.exception;

/**
 * 业务异常类
 * 用于封装业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数（仅错误消息）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 2001; // 默认业务错误码
        this.message = message;
    }

    /**
     * 参数错误异常
     *
     * @param message 错误消息
     * @return BusinessException
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(1001, message);
    }

    /**
     * 业务错误异常
     *
     * @param message 错误消息
     * @return BusinessException
     */
    public static BusinessException businessError(String message) {
        return new BusinessException(2001, message);
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置错误码
     *
     * @param code 错误码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 设置错误消息
     *
     * @param message 错误消息
     */
    public void setMessage(String message) {
        this.message = message;
    }
}