package com.github.rblessings.projects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Utility class for centralized, reusable validation logic.
 * This class leverages functional interfaces for lazy evaluation of error messages.
 */
public final class ProjectValidators {
    private ProjectValidators() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Validates that the provided string is non-null and not blank.
     *
     * @param value        the string to validate
     * @param errorMessage supplier for the error message if validation fails
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
     * @param errorMessage supplier for the error message if validation fails
     */
    public static void requireNonNullAndNonNegative(BigDecimal value, Supplier<String> errorMessage) {
        Objects.requireNonNull(value, errorMessage.get());
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    /**
     * Validates that the provided collection is non-null and does not contain any null elements.
     *
     * @param collection   the collection to validate
     * @param errorMessage supplier for the error message if validation fails
     * @param <T>          the type of elements in the collection
     */
    public static <T> void requireNonNullAndNoNullElements(Collection<T> collection, Supplier<String> errorMessage) {
        Objects.requireNonNull(collection, errorMessage.get());
        if (collection.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    /**
     * Validates that the provided integer value is non-negative.
     *
     * @param value        the integer to validate
     * @param errorMessage supplier for the error message if validation fails
     */
    public static void requireNonNegative(int value, Supplier<String> errorMessage) {
        if (value < 0) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }
}
