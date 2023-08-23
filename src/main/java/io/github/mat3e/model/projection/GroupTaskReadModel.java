package io.github.mat3e.model.projection;

import io.github.mat3e.model.Task;

public class GroupTaskReadModel {
    private String description;
    private boolean done;

    public GroupTaskReadModel(Task task){
        description = task.getDescription();
        done = task.isDone();
    }

    public GroupTaskReadModel(String description, boolean done){
        this.description = description;
        this.done = done;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(final boolean done) {
        this.done = done;
    }
}
