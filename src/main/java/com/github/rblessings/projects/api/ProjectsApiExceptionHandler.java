package com.github.rblessings.projects.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * Global exception handler for project-related exceptions thrown by REST controllers.
 *
 * <p>This class ensures consistent error formatting using the {@link ApiResponse}.</p>
 */
@RestControllerAdvice
public class ProjectsApiExceptionHandler {

    /**
     * Handles {@link WebExchangeBindException} and returns a {@link HttpStatus#BAD_REQUEST} response
     * with formatted validation error messages.
     *
     * @param ex the validation exception containing field errors
     * @return a {@link Mono} wrapping a {@link ResponseEntity} with the error messages
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<String>>> handleValidationExceptions(WebExchangeBindException ex) {
        var errorMessages = collectErrorMessages(ex);

        final var httpStatus = HttpStatus.BAD_REQUEST;
        var response = ApiResponse.<String>error(httpStatus.value(), errorMessages);
        return Mono.just(new ResponseEntity<>(response, httpStatus));
    }

    /**
     * Collects the error messages from the {@link WebExchangeBindException}.
     *
     * @param ex the validation exception
     * @return a formatted string of validation error messages
     */
    private static String collectErrorMessages(WebExchangeBindException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Handles all other exceptions and returns a generic error response.
     *
     * @param ex the exception that was thrown
     * @return a standardized API response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<String>>> handleGenericException(Exception ex) {
        final var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        var response = ApiResponse.<String>error(httpStatus.value(), ex.getMessage());
        return Mono.just(new ResponseEntity<>(response, httpStatus));
    }
}
