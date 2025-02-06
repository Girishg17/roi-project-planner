package com.github.rblessings.projects;

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
    public Mono<ApiResponse<List<ProjectDTO>>> createNewProjects(
            @Valid @RequestBody Flux<CreateNewProjectsRequest> requestFlux) {
        logger.info("Received request to create new projects");
        return requestFlux
                .doOnNext(request -> logger.debug("Processing project: {}", request))
                .map(request -> ProjectEntity.createNewProject(
                        request.name(),
                        request.requiredCapital(),
                        request.profit()))
                .collectList()
                .flatMap(projects -> {
                    logger.info("Saving {} projects", projects.size());
                    return projectService.addAll(projects)
                            .collectList()
                            .doOnSuccess(result -> logger.info("Successfully created {} projects", result.size()))
                            .map(result -> ApiResponse.success(HttpStatus.CREATED.value(), result));
                })
                .doOnError(error -> logger.error("Error occurred while creating projects", error));
    }
}
