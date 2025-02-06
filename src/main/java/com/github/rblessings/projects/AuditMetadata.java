package com.github.rblessings.projects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents audit metadata for tracking creation and modification timestamps.
 * Integrates with Spring Data’s auditing framework to manage these timestamps automatically.
 */
public record AuditMetadata(
        @CreatedDate Instant createdAt,
        @LastModifiedDate Instant updatedAt
) implements Serializable {

    /**
     * Returns an {@code AuditMetadata} with null timestamps,
     * to be automatically set and updated by Spring Data auditing.
     */
    public static AuditMetadata empty() {
        return new AuditMetadata(null, null);
    }
}
