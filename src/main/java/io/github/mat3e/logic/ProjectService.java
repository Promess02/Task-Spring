package io.github.mat3e.logic;

import io.github.mat3e.TaskConfigurationProperties;
import io.github.mat3e.model.Project;
import io.github.mat3e.model.ProjectStep;
import io.github.mat3e.model.Task;
import io.github.mat3e.model.TaskGroup;
import io.github.mat3e.model.projection.*;
import io.github.mat3e.repos.ProjectRepository;
import io.github.mat3e.repos.TaskGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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

        project.getSteps().stream().max(Comparator.comparing(ProjectStep::getDaysToDeadline))
                .ifPresent(step -> minDeadline = LocalDateTime.now().plusDays(Integer.max(step.getDaysToDeadline(),-step.getDaysToDeadline())));

        if(minDeadline.isAfter(deadline)) throw new IllegalArgumentException("Given deadline is after the deadline set by the project");

        TaskGroup result = projectRepository.findById(projectId)
                .map(project1 -> {
                    var targetGroup = new TaskGroup();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project1.getSteps().stream()
                                    .map(projectStep -> projectStep.toTask(deadline)).collect(Collectors.toSet())
                    );
                    return targetGroup;
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));

        return new GroupReadModel(result);

    }

}
