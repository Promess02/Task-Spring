package io.github.mat3e.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.mat3e.model.Task;
import io.github.mat3e.repos.TaskRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(TaskController.class)
@ActiveProfiles("integration")
public class TaskControllerLightIntTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);


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

    @Test
    @DisplayName("checking if return not found response if the updated id is missing")
    void httpPut_updateTaskMissingID() throws JSONException {
        when(taskRepository.existsById(anyInt()))
                .thenReturn(false);

        var json = new JSONObject();
        json.put("description", "update desc");

        try {
            mockMvc.perform(MockMvcRequestBuilders.put("/tasks/34")
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(json.toString()))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


}
