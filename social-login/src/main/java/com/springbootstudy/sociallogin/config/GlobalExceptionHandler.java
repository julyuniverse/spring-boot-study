package com.springbootstudy.sociallogin.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@RestControllerAdvice // view를 구현하지 않고 rest api로만 개발할 때는 @RestControllerAdvice를 사용
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("[handleCustomException] deviceId: {}", request.getHeader("Device-Id"));
        log.error("[handleCustomException] Exception StackTrace: {", e);
        log.error("}");
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<?> handleException(Exception e, HttpServletRequest request) {
        log.error("[handleCustomException] deviceId: {}", request.getHeader("Device-Id"));
        log.error("[handleException] Exception StackTrace: {", e);
        log.error("}");
        return ErrorResponse.toResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, "EXCEPTION");
    }
}
