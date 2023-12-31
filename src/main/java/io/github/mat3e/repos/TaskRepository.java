package io.github.mat3e.repos;

import io.github.mat3e.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAll();

    Page<Task> findAll(Pageable page);
    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);

    Optional<Task> findById(Integer id);

    boolean existsById(Integer id);

    List<Task> findByDone(@Param("state") boolean done);

    Task save(Task entity);
}
