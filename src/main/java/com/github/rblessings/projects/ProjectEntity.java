package com.github.rblessings.projects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable record representing a project with a unique name,
 * a required capital to initiate the project, and the profit yielded upon completion.
 */
@Document(collection = "projects")
public record ProjectEntity(
        @Id String id,
        @Indexed(unique = true) String name, // Optimized for lookup performance.
        BigDecimal requiredCapital,
        BigDecimal profit,
        AuditMetadata auditMetadata,
        @Version Long version // For optimistic locking.
) {

    public ProjectEntity {
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
    }

    /**
     * Creates a new {@code ProjectEntity} with null ID, version, and empty
     * {@code AuditMetadata}, to be populated by Spring Data upon persistence.
     */
    public static ProjectEntity createNewProject(String name, BigDecimal requiredCapital, BigDecimal profit) {
        return new ProjectEntity(null, name, requiredCapital, profit, AuditMetadata.empty(), null);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProjectEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
