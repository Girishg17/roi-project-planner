package com.github.rblessings.projects.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Utility class for centralized and reusable validation logic.
 */
public final class Validators {

    private Validators() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates that the provided string is non-null and not blank.
     *
     * @param value        the string to validate
     * @param errorMessage a supplier providing the error message if validation fails
     * @throws IllegalArgumentException if the string is null or blank
     */
    public static void requireNonNullOrBlank(String value, Supplier<String> errorMessage) {
        Objects.requireNonNull(value, errorMessage.get());
        if (value.isBlank()) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    /**
     * Validates that the provided BigDecimal is non-null and non-negative.
     *
     * @param value        the BigDecimal to validate
     * @param errorMessage a supplier providing the error message if validation fails
     * @throws IllegalArgumentException if the value is null or negative
     */
    public static void requireNonNullAndNonNegative(BigDecimal value, Supplier<String> errorMessage) {
        Objects.requireNonNull(value, errorMessage.get());
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    /**
     * Validates that the collection is non-null, non-empty, and contains no null elements.
     *
     * @param collection   the collection to validate
     * @param errorMessage a supplier providing the error message if validation fails
     * @param <T>          the type of elements in the collection
     * @throws IllegalArgumentException if the collection is null, empty, or contains null elements
     */
    public static <T> void requireNonNullAndNoNullElements(Collection<T> collection, Supplier<String> errorMessage) {
        Objects.requireNonNull(collection, errorMessage.get());

        if (collection.isEmpty() || collection.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    /**
     * Validates that the provided integer value is non-negative.
     *
     * @param value        the integer to validate
     * @param errorMessage a supplier providing the error message if validation fails
     * @throws IllegalArgumentException if the value is negative
     */
    public static void requireNonNegative(int value, Supplier<String> errorMessage) {
        if (value < 0) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    /**
     * Validates that the provided value is non-null.
     *
     * @param value        the value to validate
     * @param errorMessage a supplier providing the error message if validation fails
     * @throws IllegalArgumentException if the value is null
     */
    public static <T> void requireNonNull(T value, Supplier<String> errorMessage) {
        if (value == null) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }
}
