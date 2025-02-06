package com.github.rblessings.analytics;

import com.github.rblessings.projects.ProjectDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Immutable record representing a query to maximize capital.
 * Contains the list of available projects, the maximum number of projects that can be completed,
 * and the initial capital.
 */
public record CapitalMaximizationQuery(
        List<ProjectDTO> availableProjects,
        int maxProjects,
        BigDecimal initialCapital) {

    public CapitalMaximizationQuery {
        // Validate that the list of projects is not null and does not contain null elements.
        Objects.requireNonNull(availableProjects, "Available projects list must not be null");
        if (availableProjects.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Available projects list must not contain null elements");
        }

        // Validate that maxProjects is non-negative.
        if (maxProjects < 0) {
            throw new IllegalArgumentException("Max projects must be non-negative");
        }

        // Validate initial capital: must not be null and non-negative.
        Objects.requireNonNull(initialCapital, "Initial capital must not be null");
        if (initialCapital.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial capital must be non-negative");
        }
    }
}
