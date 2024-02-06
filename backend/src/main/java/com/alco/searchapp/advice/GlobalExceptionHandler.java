package com.alco.searchapp.advice;

import com.alco.searchapp.dto.ErrorResponseDto;
import com.alco.searchapp.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleProductNotFoundException(ProductNotFoundException ex) {
        return Mono.just(getResponse(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleInternalError(Exception ex) {
        log.error(String.format("Internal error: %s", ex.getMessage()), ex);
        return Mono.just(getResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<ErrorResponseDto> getResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new ErrorResponseDto(message));
    }
}