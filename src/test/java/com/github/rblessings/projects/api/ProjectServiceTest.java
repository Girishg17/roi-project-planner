package com.github.rblessings.projects.api;

import com.github.rblessings.projects.model.AuditMetadata;
import com.github.rblessings.projects.model.ProjectDTO;
import com.github.rblessings.projects.model.ProjectEntity;
import com.github.rblessings.projects.model.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

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

        when(projectRepository.saveAll(projects)).thenReturn(Flux.just(projectEntity1, projectEntity2));

        // When
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
                .expectErrorMatches(throwable -> throwable instanceof NullPointerException
                        && throwable.getMessage().equals("Projects cannot be null or empty"))
                .verify();
    }

    @Test
    void testFindById_ProjectFound() {
        // Given
        String projectId = "1";

        when(projectRepository.findById(projectId)).thenReturn(Mono.just(projectEntity1));

        // When
        Mono<ProjectDTO> result = projectService.findById(projectId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(projectDTO -> projectDTO.name().equals("Project 1"))
                .verifyComplete();

        Mockito.verify(projectRepository).findById(projectId);
    }

    @Test
    void testFindById_ProjectNotFound() {
        // Given
        String projectId = "2";

        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        // When
        Mono<ProjectDTO> result = projectService.findById(projectId);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NoSuchElementException
                        && throwable.getMessage().contains("Project not found for ID: 2"))
                .verify();

        Mockito.verify(projectRepository).findById(projectId);
    }
}
