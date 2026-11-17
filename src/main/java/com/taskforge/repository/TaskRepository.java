
package com.taskforge.repository;

import com.taskforge.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    void saveAll(List<Task> tasks);

    List<Task> findAll();

    Optional<Task> findById(String taskId);

    void deleteById(String taskId);
}
