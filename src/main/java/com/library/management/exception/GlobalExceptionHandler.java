package com.library.management.exception;

import com.library.management.dto.ErrorDetailsDto;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetailsDto> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorDetailsDto);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetailsDto> handleBadRequest(BadRequestException ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDetailsDto);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetailsDto> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDetailsDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailsDto> handleGeneralException(Exception ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDetailsDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid username or password");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            org.springframework.security.authorization.AuthorizationDeniedException.class,
            org.springframework.security.access.AccessDeniedException.class})
    public ResponseEntity<ErrorDetailsDto> handleForbiddenException(Exception ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorDetailsDto);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDetailsDto> handleIllegalStateException(IllegalStateException ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDetailsDto);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDetailsDto> handleNullPointerException(NullPointerException ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "A null pointer exception occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDetailsDto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetailsDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Invalid argument: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDetailsDto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetailsDto> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Constraint violation: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDetailsDto);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetailsDto> handleRuntimeException(RuntimeException ex) {
        ErrorDetailsDto ErrorDetailsDto = new ErrorDetailsDto(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Runtime exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDetailsDto);
    }
}
