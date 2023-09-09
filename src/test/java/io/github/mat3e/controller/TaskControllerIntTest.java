package io.github.mat3e.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.mat3e.model.Task;
import io.github.mat3e.repos.TaskRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.hamcrest.core.StringContains;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
public class TaskControllerIntTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);


    @Test
    void httpGet_returnsGivenTask() {

        int id = taskRepository.save(new Task("foo", LocalDateTime.now())).getId();
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/tasks/" + id))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void httpGet_returnsAllTask(){
        taskRepository.save(new Task("foo", LocalDateTime.now()));
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/tasks/"))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void httpPost_returnsGivenTask() throws JSONException {
        var description = "new desc";
        var jsonObject = new JSONObject();
        jsonObject.put("description", description);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(jsonObject.toString()))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().string(containsString("new desc")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void httpPost_updateTask() throws JSONException {

        int id = taskRepository.save(new Task("old desc", null, null)).getId();
        var json = new JSONObject();
        json.put("description", "update desc");
        try {
            mockMvc.perform(MockMvcRequestBuilders.put("/tasks/" + id)
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(json.toString()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string(containsString("update desc")));
        }catch (Exception e){
            throw new RuntimeException(e + " failed updating task");
        }
    }



}
