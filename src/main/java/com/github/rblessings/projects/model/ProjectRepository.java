package com.github.rblessings.projects.model;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProjectRepository extends ReactiveMongoRepository<ProjectEntity, String> {

}
