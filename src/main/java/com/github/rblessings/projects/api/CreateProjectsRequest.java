package com.github.rblessings.projects.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for creating a new project. Contains the necessary details for project creation.
 */
public record CreateProjectsRequest(
        @NotBlank(message = "Project name cannot be blank")
        String name,

        @NotNull(message = "The capital required to initiate the project cannot be null")
        @DecimalMin(value = "0.00", message = "Required capital must be greater than or equal to 0")
        BigDecimal requiredCapital,

        @NotNull(message = "Expected project profit cannot be null")
        @DecimalMin(value = "0.00", message = "Profit must be greater than or equal to 0")
        BigDecimal profit
) {
}

