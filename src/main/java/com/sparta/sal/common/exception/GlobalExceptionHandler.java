package com.sparta.sal.common.exception;

import com.sparta.sal.common.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> authException(AuthException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ExceptionResponse.from(
                HttpStatus.UNAUTHORIZED, e.getMessage(), URI.create(request.getRequestURI())));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ExceptionResponse> authException(InvalidRequestException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.from(
                HttpStatus.BAD_REQUEST, e.getMessage(), URI.create(request.getRequestURI())));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionResponse> authException(ServerException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.from(
                HttpStatus.BAD_REQUEST, e.getMessage(), URI.create(request.getRequestURI())));
    }
}
