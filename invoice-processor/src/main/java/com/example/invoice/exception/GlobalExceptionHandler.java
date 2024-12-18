package com.example.invoice.exception;

import com.example.invoice.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数错误", e);
        return ApiResponse.error(400, e.getMessage());
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ApiResponse.error(400, "文件大小超过限制");
    }
    
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        log.error("系统错误", e);
        return ApiResponse.error(500, "系统错误：" + e.getMessage());
    }
} 