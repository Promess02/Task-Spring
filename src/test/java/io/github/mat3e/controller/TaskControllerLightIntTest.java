package io.github.mat3e.controller;

import io.github.mat3e.model.Task;
import io.github.mat3e.repos.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(TaskController.class)
@ActiveProfiles("integration")
public class TaskControllerLightIntTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    void httpGet_returnsGivenTask() {

        when(taskRepository.findById(anyInt()))
                .thenReturn(Optional.of(new Task("foo", LocalDateTime.now())));
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/tasks/123"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.content().string(containsString("\"description\":\"foo\"")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
