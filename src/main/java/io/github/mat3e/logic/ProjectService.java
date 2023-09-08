package io.github.mat3e.logic;

import io.github.mat3e.TaskConfigurationProperties;
import io.github.mat3e.model.Project;
import io.github.mat3e.model.Task;
import io.github.mat3e.model.TaskGroup;
import io.github.mat3e.model.projection.GroupReadModel;
import io.github.mat3e.model.projection.GroupTaskWriteModel;
import io.github.mat3e.model.projection.GroupWriteModel;
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

    private TaskGroupService service;

    private LocalDateTime minDeadline;

    public ProjectService(ProjectRepository projectRepository,
                          TaskGroupRepository taskGroupRepository,
                          TaskGroupService service,
                          TaskConfigurationProperties config) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
        this.service = service;
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


        return projectRepository.findById(projectId)
                .map(project1 -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> {
                                        var task = new GroupTaskWriteModel();
                                        task.setDescription(projectStep.getDescription());
                                        task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                        return task;}
                                            ).collect(Collectors.toSet())
                    );
                    return service.createGroup(targetGroup);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));

    }

}
