package io.github.mat3e.model.projection;

import io.github.mat3e.model.ProjectStep;


public class ProjectStepDTO {
    private String description;
    private int daysToDeadline;

    ProjectStepDTO(ProjectStep projectStep){
        description = projectStep.getDescription();
        daysToDeadline = projectStep.getDaysToDeadline();
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public int getDaysToDeadline() {
        return daysToDeadline;
    }

    public void setDaysToDeadline(int daysToDeadline) {
        this.daysToDeadline = daysToDeadline;
    }

    public ProjectStep toProjectStep(){
        return new ProjectStep(this.description, this.daysToDeadline);
    }
}
