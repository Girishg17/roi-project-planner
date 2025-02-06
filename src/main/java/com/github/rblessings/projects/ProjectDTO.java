package com.github.rblessings.projects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public record ProjectDTO(
        String id,
        String name,
        BigDecimal requiredCapital,
        BigDecimal profit,
        AuditMetadata auditMetadata,
        Long version
) implements Serializable {

    public ProjectDTO {
        // Validate ID: must not be null or blank.
        Objects.requireNonNull(id, "Project ID must not be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("Project ID must not be blank");
        }

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

        // Ensure audit metadata is provided; fields may be null, as Spring Data populates them.
        Objects.requireNonNull(auditMetadata, "Audit metadata must not be null");

        // Validate version: must not be null.
        Objects.requireNonNull(version, "Project version must not be null");
    }

    public static ProjectDTO fromEntity(ProjectEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Project entity cannot be null");
        }

        return new ProjectDTO(entity.id(), entity.name(), entity.requiredCapital(), entity.profit(),
                entity.auditMetadata(), entity.version());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProjectDTO that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
