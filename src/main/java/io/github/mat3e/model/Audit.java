package io.github.mat3e.model;

import javax.persistence.*;
import java.time.LocalDateTime;

//@MappedSuperClass - inheritance
//@Embeddable - composition
@Embeddable
public class Audit {
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    //function that runs before saving to the database
    @PrePersist
    void prePersist(){
        createdOn = LocalDateTime.now();
    }

    //runs before updating the entity in a database
    @PreUpdate
    void preMerge(){
        updatedOn = LocalDateTime.now();
    }

}
