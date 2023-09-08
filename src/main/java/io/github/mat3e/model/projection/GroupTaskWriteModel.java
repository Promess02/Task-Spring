package io.github.mat3e.model.projection;

import io.github.mat3e.model.Task;
import io.github.mat3e.model.TaskGroup;

import java.time.LocalDateTime;

//DTO - Data Transfer Object
public class GroupTaskWriteModel {
    private String description;

    private LocalDateTime deadline;

    public GroupTaskWriteModel() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public GroupTaskWriteModel(String description, LocalDateTime deadline) {
        this.description = description;
        this.deadline = deadline;
    }

    public Task toTask(final TaskGroup group){
        return new Task(description, deadline, group);
    }
}
