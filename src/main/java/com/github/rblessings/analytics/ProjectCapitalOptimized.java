package com.github.rblessings.analytics;

import com.github.rblessings.projects.ProjectDTO;

import java.math.BigDecimal;
import java.util.List;

import static com.github.rblessings.projects.ProjectValidators.requireNonNullAndNoNullElements;
import static com.github.rblessings.projects.ProjectValidators.requireNonNullAndNonNegative;

/**
 * Immutable record representing the result of a capital maximization operation.
 * Contains the list of selected projects and the final accumulated capital.
 */
public record ProjectCapitalOptimized(
        List<ProjectDTO> selectedProjects,
        BigDecimal finalCapital) {

    public ProjectCapitalOptimized {
        requireNonNullAndNoNullElements(selectedProjects, () -> "Selected projects list must not be null nor contain null elements");
        requireNonNullAndNonNegative(finalCapital, () -> "Final capital must not be null and must be non-negative");
    }
}
