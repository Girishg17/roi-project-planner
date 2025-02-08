package com.github.rblessings.projects.api;

/**
 * Standardized wrapper for API responses to ensure a consistent response structure.
 *
 * <p>
 * The HTTP status code of the response, the response data, and
 * a human-readable message describing the result (typically used to explain errors in the response).
 * </p>
 *
 * <p>This class is immutable and thread-safe, ensuring consistent behavior in concurrent environments.</p>
 */
public final class ApiResponse<T> {
    private final int statusCode;
    private final String message;
    private final T data;

    private ApiResponse(int statusCode, String message, T data) {
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException(String.format("Invalid HTTP status code: %d", statusCode));
        }
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    /**
     * Creates a success response with the provided HTTP status code and data.
     */
    public static <T> ApiResponse<T> success(int statusCode, T data) {
        if (data == null) {
            throw new IllegalArgumentException("Success response data cannot be null");
        }
        return new ApiResponse<>(statusCode, null, data);
    }

    /**
     * Creates an error response with the provided HTTP status code and the error message.
     */
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Error response message cannot be null or empty");
        }
        return new ApiResponse<>(statusCode, message, null);
    }

    @Override
    public String toString() {
        return "ApiResponse{statusCode=%d, message='%s', data=%s}".formatted(statusCode, message, data);
    }
}
