package com.example.bankcards.model.dto.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    private LocalDateTime timestamp;

    private String message;

    private HttpStatus status;

    private String error;

    private String path;
}
