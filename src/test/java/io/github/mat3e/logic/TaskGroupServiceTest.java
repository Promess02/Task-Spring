package io.github.mat3e.logic;

import io.github.mat3e.model.Project;
import io.github.mat3e.model.Task;
import io.github.mat3e.model.TaskGroup;
import io.github.mat3e.repos.TaskGroupRepository;
import io.github.mat3e.repos.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {

    @Test
    @DisplayName("checking if group of the given project id has undone tasks")
    void toggleGroup_check_if_undone_tasks_of_the_same_project() {
        //given
        var mockTaskRepository = mock(TaskRepository.class);
        var mockService = new TaskGroupService(null, mockTaskRepository);
        //when
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(true);
        //then
        var exceptionCatcher = catchThrowable(() -> mockService.toggleGroup(1));

        assertThat(exceptionCatcher)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("has undone tasks");
    }

    @Test
    @DisplayName("checking if throws exception if id of project not found")
    void toggleGroup_check_if_throws_when_id_notFound() {
        //given
        var mockTaskRepository = mock(TaskRepository.class);
        var mockRepo = mock(TaskGroupRepository.class);
        var mockService = new TaskGroupService(mockRepo, mockTaskRepository);
        //when
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(false);
        when(mockRepo.findById(anyInt())).thenReturn(Optional.empty());
        //then
        var exceptionCatcher = catchThrowable(() -> mockService.toggleGroup(1));

        assertThat(exceptionCatcher)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("given id not found");
    }

    @Test
    @DisplayName("checking if function toggles the done parameter correctly")
    void toggleGroup_check_if_toggles_correctly() {
        //given
        var mockTaskRepository = mock(TaskRepository.class);
        var mockRepo = inMemoryGroupRepository();
        var mockService = new TaskGroupService(mockRepo, mockTaskRepository);
        //when
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(false);
        var groupId = mockRepo.save(createMockTaskGroup()).getId();
        //then

        mockService.toggleGroup(groupId);
        assertThat(mockRepo.findById(groupId).get().isDone()).isEqualTo(false);
    }


    private inMemoryGroupRepository inMemoryGroupRepository(){
        return new inMemoryGroupRepository();
    }

    TaskGroup createMockTaskGroup(){
        var taskGroup = new TaskGroup();
        taskGroup.setDone(true);
        taskGroup.setDescription("cool desc");
        taskGroup.setTasks(Set.of(new Task("task1", LocalDateTime.now().plusDays(2)),new Task("task2", LocalDateTime.now().plusDays(5))));
        return taskGroup;
    }

}