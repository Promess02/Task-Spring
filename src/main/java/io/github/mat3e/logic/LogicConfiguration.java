package io.github.mat3e.logic;

import io.github.mat3e.TaskConfigurationProperties;
import io.github.mat3e.repos.ProjectRepository;
import io.github.mat3e.repos.TaskGroupRepository;
import io.github.mat3e.repos.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LogicConfiguration {
    @Bean
    ProjectService projectService(
            final ProjectRepository repository,
            final TaskGroupRepository taskGroupRepository,
            final TaskConfigurationProperties configuration){
        return new ProjectService(repository, taskGroupRepository, configuration);
    }

    @Bean
    TaskGroupService taskGroupService(
            final TaskRepository taskRepository,
            final TaskGroupRepository taskGroupRepository
            ){
        return new TaskGroupService(taskGroupRepository,taskRepository);
    }
}
