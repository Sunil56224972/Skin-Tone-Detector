package com.example.skindetector.infrastructure.rest;

import com.example.skindetector.infrastructure.exception.InvalidFileException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

/**
 * Global Exception Handler for standardized Skin Detection API error responses.
 * Replaces stack traces with clean JSON ErrorResponse objects.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFile(InvalidFileException e, HttpServletRequest request) {
        log.warn("Invalid file submitted: {} at path {}", e.getMessage(), request.getRequestURI());
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage(), request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSize(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("File size limit exceeded at path {}", request.getRequestURI());
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Maximum upload size exceeded", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e, HttpServletRequest request) {
        log.error("Internal API error occurred at path {}: ", request.getRequestURI(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected processing error occurred", request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, status);
    }
}
