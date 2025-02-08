package com.github.rblessings.projects.api;

import com.github.rblessings.projects.model.ProjectDTO;
import com.github.rblessings.projects.model.ProjectEntity;
import com.github.rblessings.projects.model.ProjectRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;
import java.util.NoSuchElementException;

import static com.github.rblessings.configuration.CacheConfiguration.PROJECT_ID_CACHE_KEY;
import static com.github.rblessings.projects.model.Validators.requireNonNullAndNoNullElements;

/**
 * Service for managing project persistence operations.
 */
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Saves multiple projects to the repository.
     *
     * @param projects the projects to save
     * @return a {@link Flux} of {@link ProjectDTO} representing the saved projects
     * @throws IllegalArgumentException if the collection is null or empty
     */
    public Flux<ProjectDTO> addAll(Iterable<ProjectEntity> projects) {
        return Mono.fromCallable(() -> {
                    requireNonNullAndNoNullElements((Collection<ProjectEntity>) projects, () -> "Projects cannot be null or empty");
                    return projects;
                })
                .subscribeOn(Schedulers.boundedElastic()) // Offload to a thread pool for blocking operations
                .flatMapMany(projectRepository::saveAll)
                .map(ProjectDTO::fromEntity);
    }

    /**
     * Retrieves a project by its ID from the repository, with caching for improved performance.
     *
     * @param id the ID of the project to retrieve
     * @return a {@link Mono} containing the {@link ProjectDTO} if found
     * @throws NoSuchElementException if no project is found with the given ID
     */
    @Cacheable(value = PROJECT_ID_CACHE_KEY, key = "#id")
    public Mono<ProjectDTO> findById(String id) {
        return projectRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found for ID: %s".formatted(id))))
                .map(ProjectDTO::fromEntity);
    }
}
