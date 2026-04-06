package com.example.skindetector.infrastructure.rest;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Standardized error response format for the Skin Detection API.
 */
@Value
@Builder
public class ErrorResponse {
    LocalDateTime timestamp;
    int status;
    String error;
    String message;
    String path;
}
