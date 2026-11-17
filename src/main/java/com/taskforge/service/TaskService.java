
package com.taskforge.service;

import com.taskforge.model.Task;
import com.taskforge.model.Priority;
import com.taskforge.model.TaskStatus;
import com.taskforge.repository.TaskRepository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskService {

    private final Map<String, Task> tasks;
    private final TaskRepository repository;

    // In-memory constructor used by existing unit tests
    public TaskService() {

        tasks = new LinkedHashMap<>();
        repository = null;
    }

    // Repository-based constructor used by the application
    public TaskService(TaskRepository repository) {

        if (repository == null) {
            throw new IllegalArgumentException(
                    "Repository cannot be null."
            );
        }

        this.repository = repository;
        this.tasks = new LinkedHashMap<>();

        loadTasks();
    }

    // Load previously saved tasks
    private void loadTasks() {

        List<Task> savedTasks = repository.findAll();

        for (Task task : savedTasks) {

            if (tasks.containsKey(task.getId())) {
                throw new IllegalStateException(
                        "Duplicate task ID found: " + task.getId()
                );
            }

            tasks.put(task.getId(), task);
        }
    }

    // Save current tasks
    private void saveTasks() {

        if (repository != null) {

            repository.saveAll(
                    List.copyOf(tasks.values())
            );
        }
    }

    // Add a new task
    public void addTask(Task task) {

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task cannot be null."
            );
        }

        if (tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException(
                    "Task already exists."
            );
        }

        tasks.put(task.getId(), task);

        saveTasks();
    }

    // Retrieve all tasks
    public List<Task> getAllTasks() {

        return List.copyOf(tasks.values());
    }

    // Find task using its ID
    public Optional<Task> findTaskById(String taskId) {

        return Optional.ofNullable(tasks.get(taskId));
    }

    // Delete a task
    public boolean deleteTask(String taskId) {

        Task removedTask = tasks.remove(taskId);

        if (removedTask != null) {
            saveTasks();
            return true;
        }

        return false;
    }

    // Update task details
    public boolean updateTask(
            String taskId,
            String title,
            String description,
            Priority priority,
            LocalDate deadline) {

        Optional<Task> result = findTaskById(taskId);

        if (result.isEmpty()) {
            return false;
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Task title cannot be empty."
            );
        }

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Priority cannot be null."
            );
        }

        Task task = result.get();

        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDeadline(deadline);

        saveTasks();

        return true;
    }

    // Update task status
    public boolean updateTaskStatus(
            String taskId,
            TaskStatus status) {

        Optional<Task> result = findTaskById(taskId);

        if (result.isEmpty()) {
            return false;
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Status cannot be null."
            );
        }

        result.get().setStatus(status);

        saveTasks();

        return true;
    }
}
