package com.lvwyh.reviewx.web.common.exception;

import com.lvwyh.reviewx.web.common.response.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("参数错误");

        log.warn("Validation failed: message={}", message);
        return ApiResponse.fail(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Constraint validation failed: message={}", e.getMessage());
        return ApiResponse.fail(400, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        log.warn("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handleJsonParseException(HttpMessageNotReadableException e) {
        Throwable cause = e.getMostSpecificCause();
        log.warn("Request JSON parse failed: message={}",
                cause == null ? e.getMessage() : cause.getMessage());
        return ApiResponse.fail(400, "请求JSON格式错误，请检查字段格式");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Void> handleHttpRequestMethodNotSupported(
            HttpServletRequest request,
            HttpRequestMethodNotSupportedException e) {
        log.warn("Request method is not supported: method={}, uri={}, from={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                e.getMessage());
        return ApiResponse.fail(405, "请求方法不支持");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        log.error("Unexpected system exception", e);
        return ApiResponse.fail(500, "系统内部错误");
    }
}
