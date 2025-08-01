package com.iam.user.exception;

import com.iam.common.exception.CustomExceptions;
import com.iam.common.response.ApiResponse;
import com.iam.user.config.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UserExceptionHandler {

    @ExceptionHandler(CustomExceptions.UserNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleUserNotFound(CustomExceptions.UserNotFoundException ex) {
        log.warn("User not found exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage())));
    }

    @ExceptionHandler(CustomExceptions.EmailAlreadyExistsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleEmailAlreadyExists(CustomExceptions.EmailAlreadyExistsException ex) {
        log.warn("Email already exists exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage())));
    }

    @ExceptionHandler(CustomExceptions.ValidationException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidationException(CustomExceptions.ValidationException ex) {
        log.warn("Validation exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidationErrors(WebExchangeBindException ex) {
        log.warn("Bean validation failed: {}", ex.getMessage());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors)));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleServerWebInput(ServerWebInputException ex) {
        log.warn("Invalid parameter: {}", ex.getMessage());
        String message = ex.getReason() != null ? ex.getReason() : "Invalid parameter format";
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(Messages.INVALID_USER_DATA)));
    }

    @ExceptionHandler(DataAccessException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDataAccessException(DataAccessException ex) {
        log.error("Database access error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Database operation failed")));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(Messages.INTERNAL_SERVER_ERROR)));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGeneral(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(Messages.INTERNAL_SERVER_ERROR)));
    }
}