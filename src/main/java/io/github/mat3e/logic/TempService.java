package io.github.mat3e.logic;

import io.github.mat3e.model.Task;
import io.github.mat3e.repos.TaskGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TempService {
    @Autowired
    void temp(TaskGroupRepository repository){
        repository.findAll()
                .stream()
                .flatMap(taskGroup -> taskGroup.getTasks().stream())
                .map(Task::getDescription)
                .collect(Collectors.toList());
    }
}
