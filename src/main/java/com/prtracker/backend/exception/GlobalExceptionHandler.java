package com.prtracker.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorDto> handleMissingHeader(MissingRequestHeaderException ex) {
        logger.warn("Missing header: {}", ex.getHeaderName());
        ErrorDto error = new ErrorDto(400, "Required header '" + ex.getHeaderName() + "' is missing");
        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorDto> handleWebClientResponseException(WebClientResponseException ex) {
        int status = ex.getStatusCode().value();
        String body = ex.getResponseBodyAsString();

        logger.error("GitHub API error: status={}, body={}", status, body, ex);

        ErrorDto error = new ErrorDto(status, body);
        return ResponseEntity
                .status(status)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        logger.error("Unexpected error in API", ex);
        ErrorDto error = new ErrorDto(500, "Internal server error");
        return ResponseEntity
                .status(500)
                .body(error);
    }

}
