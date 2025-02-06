package com.github.rblessings.projects;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static com.github.rblessings.configuration.CacheConfiguration.PROJECT_ID_CACHE_KEY;

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
     * @param projects The projects to save.
     * @return A {@link Flux} of {@link ProjectDTO} representing the saved projects.
     * @throws IllegalArgumentException if the collection is empty or null.
     */
    public Flux<ProjectDTO> addAll(Iterable<ProjectEntity> projects) {
        return Mono.justOrEmpty(projects)
                .filter(projectIterable -> projectIterable.iterator().hasNext())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Projects cannot be null or empty")))
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
    public Mono<ProjectDTO> findById(final String id) {
        return projectRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found for ID: %s".formatted(id))))
                .map(ProjectDTO::fromEntity);
    }
}
