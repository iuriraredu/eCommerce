package br.com.iuriraredu.ecommerce.exception;

import br.com.iuriraredu.ecommerce.dto.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(final ResourceNotFoundException e, final HttpServletRequest request) {
        final HttpStatus status = HttpStatus.NOT_FOUND;
        final StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Resource not found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> businessException(final BusinessException e, final HttpServletRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Business rule violation",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    // Covers BadCredentialsException, DisabledException, LockedException, etc. — everything Spring
    // Security throws during authenticationManager.authenticate(). Without this handler, a failed
    // login fell through to the generic handler below and returned 500 instead of 401.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardError> authenticationException(final AuthenticationException e, final HttpServletRequest request) {
        final HttpStatus status = HttpStatus.UNAUTHORIZED;
        final StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Authentication failed",
                // Generic message on purpose: don't reveal whether it was the login or the password
                // that was wrong — this prevents someone from using the error to enumerate registered emails.
                "Invalid login or password",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();

        // Collect all fields that failed validation
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors); // Returns 422
    }

    // Guard handler: any exception not explicitly mapped above (NPE, integration error,
    // unforeseen bug) lands here. The full stacktrace goes to the server log; the client only
    // gets a generic message, never the original exception or its internal details.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleUnexpectedException(final Exception e, final HttpServletRequest request) {
        log.error("Unexpected error while processing request {}", request.getRequestURI(), e);

        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Internal server error",
                "An unexpected error occurred. Please try again or contact support if the problem persists.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}
