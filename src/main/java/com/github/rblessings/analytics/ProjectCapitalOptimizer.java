package com.github.rblessings.analytics;

import com.github.rblessings.projects.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The ProjectCapitalOptimizer class provides a method to select up to k projects from a list
 * such that the final capital is maximized. The algorithm uses a greedy approach in which at each
 * step, all projects affordable with the current capital are added to a max-heap, and the project
 * with the highest profit is chosen.
 *
 * <p>The computation is wrapped in a Reactor {@code Mono} and offloaded to a parallel scheduler.</p>
 */
public class ProjectCapitalOptimizer {

    private static final Logger logger = LoggerFactory.getLogger(ProjectCapitalOptimizer.class);

    /**
     * Maximizes the final capital based on the given query.
     *
     * @param query the capital maximization query containing available projects, maximum selections,
     *              and initial capital.
     * @return a {@code Mono} emitting a {@link ProjectCapitalOptimized} result containing the list of
     * selected projects and the final capital.
     * @throws IllegalArgumentException if the query or its available projects list is null.
     */
    public Mono<ProjectCapitalOptimized> maximizeCapital(CapitalMaximizationQuery query) {
        if (query == null || query.availableProjects() == null) {
            logger.error("Received null query or available projects list.");
            return Mono.error(new IllegalArgumentException("Capital maximization query must not be null"));
        }

        logger.info("Starting capital maximization with initial capital: {}", query.initialCapital());
        // Offload the CPU-bound computation to a parallel scheduler.
        return Mono.fromCallable(() -> computeMaximizedCapital(query))
                .subscribeOn(Schedulers.parallel())
                .doOnSuccess(result -> logger.info("Capital maximization complete. Final capital: {}", result.finalCapital()))
                .doOnError(error -> logger.error("Error during capital maximization", error));
    }

    /**
     * Performs the core greedy algorithm to select projects and maximize capital.
     *
     * @param query the capital maximization query.
     * @return a {@link ProjectCapitalOptimized} instance with the selected projects and final capital.
     */
    private ProjectCapitalOptimized computeMaximizedCapital(CapitalMaximizationQuery query) {
        List<Project> projects = new ArrayList<>(query.availableProjects());
        logger.debug("Number of available projects: {}", projects.size());

        // Sort projects by required capital in ascending order.
        projects.sort(Comparator.comparing(Project::requiredCapital));
        logger.debug("Projects sorted by required capital.");

        // Max-heap to choose the project with the highest profit among those affordable.
        PriorityQueue<Project> profitMaxHeap = new PriorityQueue<>(Comparator.comparing(Project::profit).reversed());

        List<Project> selectedProjects = new ArrayList<>();
        BigDecimal currentCapital = query.initialCapital();
        int totalProjects = projects.size();
        int projectIndex = 0;

        for (int i = 0; i < query.maxProjects(); i++) {
            // Log the current iteration and capital.
            logger.debug("Iteration {}: Current capital: {}", i, currentCapital);

            // Add all projects whose required capital is within the current capital.
            while (projectIndex < totalProjects
                    && projects.get(projectIndex).requiredCapital().compareTo(currentCapital) <= 0) {
                Project project = projects.get(projectIndex);
                profitMaxHeap.offer(project);
                logger.debug("Project {} (profit: {}) is affordable and added to the heap.", project.name(), project.profit());
                projectIndex++;
            }

            // If no projects are available to start, break early.
            if (profitMaxHeap.isEmpty()) {
                logger.info("No further projects can be selected with current capital: {}", currentCapital);
                break;
            }

            // Select the project with the highest profit.
            Project chosenProject = profitMaxHeap.poll();
            selectedProjects.add(chosenProject);
            currentCapital = currentCapital.add(chosenProject.profit());
            logger.info("Selected project {}. Updated capital: {}", chosenProject.name(), currentCapital);
        }

        return new ProjectCapitalOptimized(selectedProjects, currentCapital);
    }
}
