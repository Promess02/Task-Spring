package io.github.mat3e.logic;

import io.github.mat3e.model.TaskGroup;
import io.github.mat3e.repos.TaskGroupRepository;

import java.util.*;

public class inMemoryGroupRepository implements TaskGroupRepository {
    private int index = 0;
    private Map<Integer, TaskGroup> map = new HashMap<>();

    public int count() {
        return map.values().size();
    }

    @Override
    public List<TaskGroup> findAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public Optional<TaskGroup> findById(Integer id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public TaskGroup save(TaskGroup entity) {
        if (entity.getId() == 0) {
            try {
                var field = TaskGroup.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(entity, ++index);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        map.put(entity.getId(), entity);

        return entity;

    }
    @Override
    public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {
        return map.values().stream()
                .filter(taskGroup -> !taskGroup.isDone())
                .anyMatch(group -> group.getProject() != null && group.getProject().getId() == projectId);

    }
}
