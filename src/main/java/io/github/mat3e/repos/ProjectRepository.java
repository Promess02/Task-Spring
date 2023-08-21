package io.github.mat3e.repos;

import io.github.mat3e.model.Project;
import io.github.mat3e.model.TaskGroup;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    List<Project> findAll();
    Optional<Project> findById(Integer id);
    Project save(Project entity);
}
