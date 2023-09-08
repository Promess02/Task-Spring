package io.github.mat3e.model;

import io.github.mat3e.model.projection.GroupTaskReadModel;
import io.github.mat3e.model.projection.GroupTaskWriteModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_steps")
public class ProjectStep {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank(message = "Project step's description must not be empty")
    private String description;

    @Column(name = "days_to_deadline")
    private int daysToDeadline;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectStep() {
    }

    public ProjectStep(String description, int daysToDeadline) {
        this.description = description;
        this.daysToDeadline = daysToDeadline;
    }

    public int getId() {
        return id;
    }

    void setId(final int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDaysToDeadline() {
        return daysToDeadline;
    }

    public void setDaysToDeadline(int daysToDeadline) {
        this.daysToDeadline = daysToDeadline;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task toTask(LocalDateTime deadline){
        return new GroupTaskWriteModel(this.description, deadline.minusDays(Integer.max(daysToDeadline, -daysToDeadline))).toTask(null);
    }

    public GroupTaskWriteModel toTaskWriteModel(LocalDateTime deadline){
        return new GroupTaskWriteModel(this.description, deadline.minusDays(Integer.max(daysToDeadline, -daysToDeadline)));
    }

    public GroupTaskReadModel toTaskReadModel(boolean done){
        return new GroupTaskReadModel(this.description, done);
    }

}
