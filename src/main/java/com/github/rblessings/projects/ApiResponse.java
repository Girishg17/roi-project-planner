package com.github.rblessings.projects;

/**
 * Standardized wrapper for API responses.
 *
 * <p>Ensures a consistent response structure, encapsulating the following: </p>
 *
 * <p>
 * - <b>statusCode</b>: The HTTP status code of the response. <br/>
 * - <b>data</b>: The response data, which can vary (e.g., project details upon creation).<br/>
 * - <b>message</b>: A human-readable message describing the result (typically used in error scenarios).
 * </p>
 *
 * <p>This class is designed to be immutable and thread-safe, providing consistency in concurrent environments.</p>
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
     *
     * @param statusCode the HTTP status code
     * @param data       the response data
     * @param <T>        the type of the response data
     * @return a new {@code ApiResponse<T>} instance representing a success response
     * @throws IllegalArgumentException when data is null
     */
    public static <T> ApiResponse<T> success(int statusCode, T data) {
        if (data == null) {
            throw new IllegalArgumentException("Success response data cannot be null");
        }
        return new ApiResponse<>(statusCode, null, data);
    }

    /**
     * Creates an error response with the provided HTTP status code and message.
     *
     * @param statusCode the HTTP status code
     * @param message    a human-readable error message
     * @param <T>        the type of the response data (use {@code Void} if no data is required)
     * @return a new {@code ApiResponse} instance representing an error response
     * @throws IllegalArgumentException when the error message is empty or null
     */
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Error response message cannot be null or empty");
        }
        return new ApiResponse<>(statusCode, message, null);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
