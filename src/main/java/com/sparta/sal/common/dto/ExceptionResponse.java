package com.sparta.sal.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ExceptionResponse {

    private final String timestamp = String.valueOf(LocalDateTime.now());
    private final HttpStatus status;
    private final String error;
    private final URI path;

    public static ExceptionResponse from(HttpStatus status, String error, URI path) {
        return new ExceptionResponse(status, error, path);
    }
}
