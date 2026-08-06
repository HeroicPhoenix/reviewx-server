package com.lvwyh.reviewx.web.common.exception;

import com.lvwyh.reviewx.web.common.response.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理器。
 *
 * Controller 层不需要手动 try-catch，所有异常统一在这里转换为 ApiResponse。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理 @RequestBody 对象校验失败。
     *
     * 例如 AO 中 @NotBlank、@NotNull、@Size 等校验不通过。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("参数错误");

        log.warn("Validation failed: message={}", message);
        return response(400, message);
    }

    /**
     * 处理 Controller 方法参数校验失败。
     *
     * 例如 @RequestParam 上的 @NotBlank 校验失败。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Constraint validation failed: message={}", e.getMessage());
        return response(400, e.getMessage());
    }

    /**
     * 处理业务异常。
     *
     * 业务异常携带明确 code，直接返回给前端。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        return response(e.getCode(), e.getMessage());
    }

    /**
     * 处理请求 JSON 解析失败。
     *
     * 常见于请求体 JSON 格式错误、时间格式不符合 LocalDateTime 解析规则等。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonParseException(HttpMessageNotReadableException e) {
        Throwable cause = e.getMostSpecificCause();
        log.warn("Request JSON parse failed: message={}",
                cause == null ? e.getMessage() : cause.getMessage());
        return response(400, "请求JSON格式错误，请检查字段格式");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("Upload file exceeds size limit: message={}", e.getMessage());
        return response(413, "上传文件不能超过50MB");
    }

    /**
     * 处理 HTTP 方法不支持。
     *
     * 例如接口声明 POST，但客户端用 GET 访问。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupported(
            HttpServletRequest request,
            HttpRequestMethodNotSupportedException e) {
        log.warn("Request method is not supported: method={}, uri={}, from={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                e.getMessage());
        return response(405, "请求方法不支持");
    }

    /**
     * 兜底异常处理。
     *
     * 对未知系统异常只返回统一提示，避免向前端暴露堆栈、SQL、服务器路径等敏感信息。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unexpected system exception", e);
        return response(500, "系统内部错误");
    }

    private ResponseEntity<ApiResponse<?>> response(int code, String message) {
        HttpStatus httpStatus = HttpStatus.resolve(code);
        if (httpStatus == null) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(httpStatus).body(ApiResponse.fail(code, message));
    }
}
