package com.github.rblessings.projects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private ProjectEntity projectEntity1;
    private ProjectEntity projectEntity2;

    @BeforeEach
    void setUp() {
        // Set up sample project entities
        projectEntity1 = new ProjectEntity("1", "Project 1", BigDecimal.ZERO, BigDecimal.ONE, AuditMetadata.empty(), 0L);
        projectEntity2 = new ProjectEntity("2", "Project 2", BigDecimal.ONE, BigDecimal.TWO, AuditMetadata.empty(), 0L);
    }

    @Test
    void testAddAll_Success() {
        // Given
        Iterable<ProjectEntity> projects = List.of(projectEntity1, projectEntity2);

        // When
        when(projectRepository.saveAll(projects)).thenReturn(Flux.just(projectEntity1, projectEntity2));

        Flux<ProjectDTO> result = projectService.addAll(projects);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(projectDTO -> projectDTO.name().equals("Project 1"))
                .expectNextMatches(projectDTO -> projectDTO.name().equals("Project 2"))
                .verifyComplete();

        verify(projectRepository).saveAll(projects); // Ensure the repository method was called
    }

    @Test
    void testAddAll_EmptyCollection() {
        // Given
        Iterable<ProjectEntity> emptyProjects = Collections.emptyList();

        // When
        Flux<ProjectDTO> result = projectService.addAll(emptyProjects);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException
                        && throwable.getMessage().equals("Projects cannot be null or empty"))
                .verify();
    }

    @Test
    void testAddAll_NullCollection() {
        // Given
        Iterable<ProjectEntity> nullProjects = null;

        // When
        Flux<ProjectDTO> result = projectService.addAll(nullProjects);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException
                        && throwable.getMessage().equals("Projects cannot be null or empty"))
                .verify();
    }
}
