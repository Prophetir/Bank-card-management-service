package com.example.bankcards.exception.globalHandler;

import com.example.bankcards.exception.exceptions.CreateException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.exception.exceptions.NotFoundProfileForCardCreateException;
import com.example.bankcards.exception.exceptions.TransactionException;
import com.example.bankcards.model.dto.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = NotFoundException.class)
    public ResponseEntity<ExceptionResponse> notFoundExceptionHandler(NotFoundException exception,
                                                                      HttpServletRequest request)  {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ExceptionResponse.builder()
                        .timestamp(LocalDateTime.from(Instant.now()))
                        .message(exception.getMessage())
                        .status(HttpStatus.NOT_FOUND)
                        .error("NOT_FOUND")
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ExceptionResponse> transactionExceptionHandler(TransactionException exception,
                                                                         HttpServletRequest request)  {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .timestamp(LocalDateTime.from(Instant.now()))
                        .message(exception.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .error("BAD_REQUEST")
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(CreateException.class)
    public ResponseEntity<ExceptionResponse> createExceptionHandler(CreateException exception,
                                                                    HttpServletRequest request)  {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionResponse.builder()
                        .timestamp(LocalDateTime.from(Instant.now()))
                        .message(exception.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .error("BAD_REQUEST")
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(NotFoundProfileForCardCreateException.class)
    public ResponseEntity<ExceptionResponse> notFoundProfileForCreateCardHandler(
            NotFoundProfileForCardCreateException exception, HttpServletRequest request) {

        return ResponseEntity.badRequest().body(ExceptionResponse.builder()
                .timestamp(LocalDateTime.from(Instant.now()))
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .error("PROFILE_REQUIRED")
                .path(request.getRequestURI())
                .build()
        );
    }
}
