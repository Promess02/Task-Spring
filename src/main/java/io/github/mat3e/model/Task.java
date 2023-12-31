package io.github.mat3e.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
// Inheritance:
// public class Task extends Audit
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank(message = "Task's description must not be empty")
    private String description;
    private boolean done;
    private LocalDateTime deadline;
    @Embedded
    private Audit audit = new Audit();
    @ManyToOne
    @JoinColumn(name = "task_group_id")
    private TaskGroup group;
    // column name overriding
    //    @AttributeOverrides(
//            {
//                    @AttributeOverride(column = @Column(name = "updatedOn"), name="updatedOn")
//
//            }
//    )
    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }


    public Task() {
    }
    public Task(String description, LocalDateTime deadline, TaskGroup group){
        this.description = description;
        this.deadline = deadline;
        if(group!=null){
            this.group = group;
        }
    }
    public Task(String description, LocalDateTime deadline) {
        this(description,deadline,null);
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

    void setDescription(final String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(final boolean done) {
        this.done = done;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    void setDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }

    TaskGroup getGroup() {
        return group;
    }

    void setGroup(final TaskGroup group) {
        this.group = group;
    }

    public void updateFrom(final Task source) {
        description = source.description;
        done = source.done;
        deadline = source.deadline;
        group = source.group;
    }
}

