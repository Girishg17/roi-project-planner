package com.github.rblessings.projects.model;

import com.github.rblessings.configuration.MongoConfiguration;
import com.github.rblessings.configuration.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataMongoTest
@Import({MongoConfiguration.class, TestcontainersConfiguration.class})
class ProjectRepositoryTest {
    private final ProjectRepository projectRepository;

    @Autowired
    ProjectRepositoryTest(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Test
    @DisplayName("should throw OptimisticLockingFailureException when concurrent updates attempt to modify the same entity")
    void shouldThrowOptimisticLockingFailure_whenConcurrentUpdatesAttemptToModifySameEntity() {
        // Given: A project entity is saved to the repository
        Mono<ProjectEntity> persistedProjectMono = projectRepository.save(
                ProjectEntity.createNewProject("Project A", BigDecimal.ONE, BigDecimal.TWO));

        // When & Then: Attempt to update with an outdated version should trigger an OptimisticLockingFailureException
        persistedProjectMono.flatMap(persistedProject -> {
                    var staleProject = new ProjectEntity(
                            persistedProject.id(),
                            persistedProject.name(),
                            persistedProject.requiredCapital(),
                            persistedProject.profit(),
                            persistedProject.auditMetadata(),
                            persistedProject.version() - 1 // Outdated version for concurrency conflict
                    );
                    return projectRepository.save(staleProject); // Attempting a concurrent update
                })
                .as(StepVerifier::create)
                .expectError(OptimisticLockingFailureException.class)
                .verify();
    }

    @Test
    @DisplayName("should persist a project with valid details and auto-populate fields")
    void shouldPersistProject_whenValidDetailsProvided() {
        // Given: A new project entity with valid details
        var entity = ProjectEntity.createNewProject("Project A", BigDecimal.ONE, BigDecimal.TWO);

        // When: The project is saved to the repository
        Mono<ProjectEntity> persistedProjectMono = projectRepository.save(entity);

        // Then: Verify the project is persisted with populated fields (ID, audit metadata, version)
        StepVerifier.create(persistedProjectMono)
                .expectNextMatches(persistedProject -> {
                    assertThat(persistedProject.id()).isNotNull();
                    assertThat(persistedProject.name()).isEqualTo("Project A");
                    assertThat(persistedProject.requiredCapital()).isEqualTo(BigDecimal.ONE);
                    assertThat(persistedProject.profit()).isEqualTo(BigDecimal.TWO);
                    assertThat(persistedProject.auditMetadata().createdAt()).isNotNull();
                    assertThat(persistedProject.auditMetadata().updatedAt()).isNotNull();
                    assertThat(persistedProject.version()).isNotNull();
                    return true;
                })
                .verifyComplete();
    }
}
