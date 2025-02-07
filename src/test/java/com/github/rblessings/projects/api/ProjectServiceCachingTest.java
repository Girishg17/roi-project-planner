package com.github.rblessings.projects.api;

import com.github.rblessings.projects.model.AuditMetadata;
import com.github.rblessings.projects.model.ProjectDTO;
import com.github.rblessings.projects.model.ProjectEntity;
import com.github.rblessings.projects.model.ProjectRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static com.github.rblessings.configuration.CacheConfiguration.PROJECT_ID_CACHE_KEY;

@Testcontainers
@SpringBootTest
class ProjectServiceCachingTest {
    private static final int REDIS_PORT = 6379;

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.4.2"))
                    .waitingFor(Wait.forListeningPort())
                    .withExposedPorts(REDIS_PORT);

    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT));
    }

    @MockitoBean
    private ProjectRepository projectRepository;

    private final CacheManager cacheManager;
    private final ProjectService projectService;

    private ProjectEntity projectEntity;

    @Autowired
    public ProjectServiceCachingTest(CacheManager cacheManager, ProjectService projectService) {
        this.cacheManager = cacheManager;
        this.projectService = projectService;
    }

    @BeforeEach
    void setUp() {
        final Cache usersCache = cacheManager.getCache(PROJECT_ID_CACHE_KEY);
        if (usersCache != null) {
            usersCache.clear();
        }

        projectEntity = new ProjectEntity("1", "Project 1", new BigDecimal("100.00"),
                new BigDecimal("300"), AuditMetadata.empty(), 0L);
    }

    @Test
    void testFindById_shouldCacheResult() {
        Mockito.when(projectRepository.findById(Mockito.anyString())).thenReturn(Mono.just(projectEntity));

        // Given: A specific project ID
        final var projectID = "1";

        // When: Invoking the service method twice, expecting the result to be cached after the initial invocation
        ProjectDTO firstInvocationResult = projectService.findById(projectID).block();
        ProjectDTO secondInvocationResult = projectService.findById(projectID).block();

        // Then: Assert that both invocations return the same cached result, without querying the repository again.
        Assertions.assertThat(firstInvocationResult).isNotNull();
        Assertions.assertThat(secondInvocationResult).isNotNull();
        Assertions.assertThat(firstInvocationResult).isEqualTo(secondInvocationResult);

        // Verify that the repository was queried only once, confirming the caching mechanism.
        Mockito.verify(projectRepository, Mockito.times(1)).findById(Mockito.anyString());
    }
}
