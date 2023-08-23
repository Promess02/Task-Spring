package io.github.mat3e.model.projection;

import io.github.mat3e.model.Project;
import io.github.mat3e.model.ProjectStep;

import java.util.Set;
import java.util.stream.Collectors;

public class ProjectDTO {
    private String description;
    private Set<ProjectStepDTO> steps;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ProjectStepDTO> getSteps() {
        return steps;
    }

    public void setSteps(Set<ProjectStepDTO> steps) {
        this.steps = steps;
    }

    public Project toProject(){
        Project result = new Project(description,
                steps.stream().map(ProjectStepDTO::toProjectStep).collect(Collectors.toSet()));
        return result;
    }
}
