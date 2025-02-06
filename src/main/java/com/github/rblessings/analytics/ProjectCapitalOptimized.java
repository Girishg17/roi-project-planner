package com.github.rblessings.analytics;

import com.github.rblessings.projects.Project;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Immutable record representing the result of a capital maximization operation.
 * Contains the list of selected projects and the final accumulated capital.
 */
public record ProjectCapitalOptimized(
        List<Project> selectedProjects,
        BigDecimal finalCapital) {

    public ProjectCapitalOptimized {
        // Validate that the selected projects list is not null and does not contain null elements.
        Objects.requireNonNull(selectedProjects, "Selected projects list must not be null");
        if (selectedProjects.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Selected projects list must not contain null elements");
        }

        // Validate final capital: must not be null and non-negative.
        Objects.requireNonNull(finalCapital, "Final capital must not be null");
        if (finalCapital.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Final capital must be non-negative");
        }
    }
}
