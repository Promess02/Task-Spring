package io.github.mat3e.logic;

import io.github.mat3e.TaskConfigurationProperties;
import io.github.mat3e.model.Project;
import io.github.mat3e.model.Task;
import io.github.mat3e.model.TaskGroup;
import io.github.mat3e.model.projection.GroupReadModel;
import io.github.mat3e.model.projection.ProjectDTO;
import io.github.mat3e.repos.ProjectRepository;
import io.github.mat3e.repos.TaskGroupRepository;
import org.springframework.web.context.annotation.RequestScope;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//@Service
@RequestScope
public class ProjectService {
    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    private LocalDateTime minDeadline;

    public ProjectService(ProjectRepository projectRepository, TaskGroupRepository taskGroupRepository, TaskConfigurationProperties config) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }

    public List<Project> readAllProjects(){
        return projectRepository.findAll();
    }

    public void saveProject(ProjectDTO source){
        projectRepository.save(source.toProject());
    }

    public GroupReadModel createGroup(int projectId, LocalDateTime deadline){
        if(!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId))
            throw new IllegalStateException("multiple undone groups of the same project are not allowed");

        Project project = projectRepository.findById(projectId).
                orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));


        TaskGroup result = projectRepository.findById(projectId)
                .map(project1 -> {
                    var targetGroup = new TaskGroup();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> new Task(
                                            projectStep.getDescription(),
                                            deadline.plusDays(projectStep.getDaysToDeadline()))
                                    ).collect(Collectors.toSet())
                    );
                    targetGroup.setProject(project);
                    return taskGroupRepository.save(targetGroup);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));

        return new GroupReadModel(result);

    }

}
