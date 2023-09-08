package io.github.mat3e.logic;

import io.github.mat3e.TaskConfigurationProperties;
import io.github.mat3e.model.Project;
import io.github.mat3e.model.ProjectStep;
import io.github.mat3e.repos.ProjectRepository;
import io.github.mat3e.repos.TaskGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when configured to allow just 1 group and the other undone exists")
    void createGroup_noMultipleGroupsConfig_And_openGroups_throwsIllegalStateException() {
        //given
        var mockGroupRepository = groupRepositoryReturning(true);

        var mockConfig = configurationReturning(false);
        // system under test
        var toTest = new ProjectService(null, mockGroupRepository,null,mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(0, LocalDateTime.now()));

        // then assertJ

        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone groups");

        //then
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when configuration ok and no projects for a given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        //given
        var mockConfig = configurationReturning(true);
        //and
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        // system under test
        var toTest = new ProjectService(mockRepository, null,null,mockConfig);
        //when
        var exception = catchThrowable(() -> toTest.createGroup(0, LocalDateTime.now()));

        // then assertJ
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("given id not found");

        //then
    }

    @Test
    @DisplayName("should throw IllegalArgumentException should throw IllegalStateException when configured to allow just 1 group and no groups and projects for a given id")
    void createGroup_noMultipleGroupsConfig_And_noUndoneGroupExists_noProjects_throwsIllegalStateException() {
        //given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        var mockConfig = configurationReturning(true);
        //and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);
        // system under test
        var toTest = new ProjectService(mockRepository, mockGroupRepository,null,mockConfig);
        //when
        var exception = catchThrowable(() -> toTest.createGroup(0, LocalDateTime.now()));

        // then assertJ
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("given id not found");

        //then
    }

    @Test
    @DisplayName("should create new group from project")
    void createGroup_configOK_existingProject_createsAndSavesGroup(){
        //given
        var today = LocalDate.now().atStartOfDay();
        // and
        var project = projectWith("bar", Set.of(-1,-2));
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt()))
                .thenReturn(Optional.of(project));
        // and
        var inMemoryGroupRepo = inMemoryGroupRepository();
        var serviceWithInMemRepo = dummyGroupService(inMemoryGroupRepo);
        int countBeforeCall = inMemoryGroupRepo.count();
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        var toTest = new ProjectService(mockRepository,inMemoryGroupRepo,serviceWithInMemRepo,mockConfig);

        // when
        var result = toTest.createGroup(0, today);
        assertThat(result.getDescription())
                .isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).allMatch(task -> task.getDescription().equals("foo"));
        assertThat(countBeforeCall+1)
                .isEqualTo(inMemoryGroupRepo.count());
    }

    private static TaskGroupService dummyGroupService(inMemoryGroupRepository inMemoryGroupRepo) {
        return new TaskGroupService(inMemoryGroupRepo, null);
    }

    private Project projectWith(String projectDescription, Set<Integer> daysToDeadline){
        Set<ProjectStep> steps = daysToDeadline.stream()
                .map(days -> {
                    var step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("foo");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                }).collect(Collectors.toSet());
        var result = mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);
        when(result.getSteps()).thenReturn(steps);
        return result;
    }
    private inMemoryGroupRepository inMemoryGroupRepository(){
        return new inMemoryGroupRepository();
    }



    private static TaskGroupRepository groupRepositoryReturning(boolean result) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private static TaskConfigurationProperties configurationReturning(final boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }
}