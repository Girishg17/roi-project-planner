package com.github.rblessings.projects;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProjectRepository extends ReactiveMongoRepository<ProjectEntity, String> {

}
