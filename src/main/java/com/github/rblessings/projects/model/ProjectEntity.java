package com.github.rblessings.projects.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Objects;

import static com.github.rblessings.projects.model.Validators.requireNonNullAndNonNegative;
import static com.github.rblessings.projects.model.Validators.requireNonNullOrBlank;
import static java.util.Objects.requireNonNull;

/**
 * Immutable record representing a project with a unique name, required capital to initiate the project,
 * and profit upon completion.
 *
 * <p> The project name is indexed for optimized lookup performance. </p>
 */
@Document(collection = "projects")
public record ProjectEntity(
        @Id String id,
        @Indexed(unique = true) String name,
        BigDecimal requiredCapital,
        BigDecimal profit,
        AuditMetadata auditMetadata,
        @Version Long version
) {

    public ProjectEntity {
        // Validate string fields
        requireNonNullOrBlank(name, () -> "Project name must not be null or blank");

        // Validate numeric fields
        requireNonNullAndNonNegative(requiredCapital, () -> "Required capital must not be null and must be non-negative");
        requireNonNullAndNonNegative(profit, () -> "Profit must not be null and must be non-negative");

        // Validate audit metadata
        requireNonNull(auditMetadata, "Audit metadata must not be null");
    }

    /**
     * Creates a new {@code ProjectEntity} with null ID, version, and an empty
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
