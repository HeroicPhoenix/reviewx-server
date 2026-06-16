package com.lvwyh.reviewx.web.common.exception;

/**
 * 业务异常
 *
 * 说明：
 * 1. 只用于业务规则错误
 * 2. 不用于系统异常（NPE、SQL异常等）
 */
public class BusinessException extends RuntimeException {

    private final int code;

    /**
     * 默认业务错误 400
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 自定义错误码
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 带异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }

    /**
     * 带错误码 + 原因
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
