package br.com.iuriraredu.ecommerce.exception;

import br.com.iuriraredu.ecommerce.dto.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = NOT_FOUND;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Resource not found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> businessException(BusinessException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Business rule violation",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

}
