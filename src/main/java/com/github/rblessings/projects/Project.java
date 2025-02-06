package com.github.rblessings.projects;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable record representing a project with a unique name, a required capital to start,
 * and the profit yielded upon completion.
 */
public record Project(
        String name,
        BigDecimal requiredCapital,
        BigDecimal profit) {

    public Project {
        // Validate name: must not be null or blank.
        Objects.requireNonNull(name, "Project name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Project name must not be blank");
        }

        // Validate required capital: must not be null and non-negative.
        Objects.requireNonNull(requiredCapital, "Required capital must not be null");
        if (requiredCapital.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Required capital must be non-negative");
        }

        // Validate profit: must not be null and non-negative.
        Objects.requireNonNull(profit, "Profit must not be null");
        if (profit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Profit must be non-negative");
        }
    }
}
