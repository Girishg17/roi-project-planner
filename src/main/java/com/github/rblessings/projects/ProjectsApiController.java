package com.github.rblessings.projects;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/apis/v1/projects")
public class ProjectsApiController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectsApiController.class);

    private final ProjectService projectService;

    public ProjectsApiController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @CircuitBreaker(name = "projectsApi", fallbackMethod = "createNewProjectsFallback")
    @Retry(name = "projectsApi")
    @RateLimiter(name = "projectsApi")
    @Bulkhead(name = "projectsApi")
    @TimeLimiter(name = "projectsApi")
    public Mono<ApiResponse<List<ProjectDTO>>> createNewProjects(
            @Valid @RequestBody Flux<CreateProjectsRequest> requestFlux) {
        logger.info("Received request to create new projects");
        return requestFlux
                .doOnNext(request -> logger.debug("Processing project: {}", request))
                .map(this::toProjectEntity)
                .collectList()
                .flatMap(this::saveProjects)
                .doOnError(error -> logger.error("Error occurred while creating projects", error));
    }

    private ProjectEntity toProjectEntity(CreateProjectsRequest request) {
        return ProjectEntity.createNewProject(
                request.name(),
                request.requiredCapital(),
                request.profit());
    }

    private Mono<ApiResponse<List<ProjectDTO>>> saveProjects(List<ProjectEntity> projects) {
        logger.info("Saving {} projects", projects.size());
        return projectService.addAll(projects)
                .collectList()
                .doOnSuccess(result -> logger.info("Successfully created {} projects", result.size()))
                .map(result -> ApiResponse.success(HttpStatus.CREATED.value(), result));
    }

    private Mono<ApiResponse<List<ProjectDTO>>> createNewProjectsFallback(
            Flux<CreateProjectsRequest> requestFlux, Throwable t) {
        logger.error("Fallback triggered: {}", t.getMessage());
        return Mono.just(ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service temporarily unavailable. Please try again later."));
    }
}
