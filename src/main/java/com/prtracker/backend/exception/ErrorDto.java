package com.prtracker.backend.exception;

public record ErrorDto(
        int status,
        String message
) {
}
