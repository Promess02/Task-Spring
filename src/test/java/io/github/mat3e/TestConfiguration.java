package io.github.mat3e;

import io.github.mat3e.model.Task;
import io.github.mat3e.model.TaskGroup;
import io.github.mat3e.repos.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;

@Configuration
public class TestConfiguration {

    @Bean
    @Primary
    @Profile("!integration")
    DataSource testDataSource(){
        var result = new DriverManagerDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        result.setDriverClassName("org.h2.Driver");
        return result;
    }

    @Bean
    @Primary
    // @Primary - ensures that testRepo() has higher prio to be injected to other beans then other singletons of the same class
    // @ConditionalOnMissingBean - allows this bean to be injected in case of missing beans of this class
    // @Profile() - use this bean only when the app is executed on the given profile
    @Profile("integration")
    TaskRepository testRepo(){
       return new TaskRepository(){
           private Map<Integer, Task> tasks = new HashMap<>();
           @Override
           public List<Task> findAll() {
               return new ArrayList<>(tasks.values());
           }

           @Override
           public Page<Task> findAll(Pageable page) {
               return null;
           }

           @Override
           public boolean existsByDoneIsFalseAndGroup_Id(Integer groupId) {
               return false;
           }

           @Override
           public Optional<Task> findById(Integer id) {
               return Optional.ofNullable(tasks.get(id));
           }

           @Override
           public boolean existsById(Integer id) {
               return tasks.containsKey(id);
           }

           @Override
           public List<Task> findByDone(boolean done) {
               return null;
           }

           @Override
           public Task save(Task entity) {
               int key = tasks.size() + 1;
               try {
                   var field = Task.class.getDeclaredField("id");
                   field.setAccessible(true);
                   field.set(entity, key);
               } catch (NoSuchFieldException | IllegalAccessException e) {
                   throw new RuntimeException(e);
               }

               tasks.put(key, entity);
               return tasks.get(key);
           }
       };
    }
}
