package com.github.rblessings.projects;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@WebFluxTest(ProjectsApiController.class)
class ProjectsApiControllerTest {

    @MockitoBean
    private ProjectService projectService;

    @InjectMocks
    private ProjectsApiController projectsApiController;

    private final WebTestClient webTestClient;

    @Autowired
    ProjectsApiControllerTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void testCreateNewProjects_Success() {
        // Given
        var request1 = new CreateProjectsRequest("Project 1", new BigDecimal("100.00"), new BigDecimal("500.00"));
        var request2 = new CreateProjectsRequest("Project 2", new BigDecimal("150.00"), new BigDecimal("800.00"));

        Flux<CreateProjectsRequest> requestFlux = Flux.just(request1, request2);

        // ProjectDTO mock return
        var projectDTO1 = new ProjectDTO("1", "Project 1", new BigDecimal("100.00"), new BigDecimal("500.00"), AuditMetadata.empty(), 0L);
        var projectDTO2 = new ProjectDTO("2", "Project 2", new BigDecimal("150.00"), new BigDecimal("800.00"), AuditMetadata.empty(), 0L);

        // Mock the service call to return a Flux of ProjectDTO
        when(projectService.addAll(anyList())).thenReturn(Flux.just(projectDTO1, projectDTO2));

        // When & Then
        webTestClient.post()
                .uri("/apis/v1/projects")
                .body(requestFlux, Flux.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo("201")
                .jsonPath("$.data[0].id").isEqualTo("1")
                .jsonPath("$.data[0].name").isEqualTo("Project 1")
                .jsonPath("$.data[0].requiredCapital").isEqualTo(100.00)
                .jsonPath("$.data.[0].profit").isEqualTo(500.00)
                .jsonPath("$.data.[1].id").isEqualTo("2")
                .jsonPath("$.data.[1].name").isEqualTo("Project 2")
                .jsonPath("$.data.[1].requiredCapital").isEqualTo(150.00)
                .jsonPath("$.data.[1].profit").isEqualTo(800.00);
    }
}
