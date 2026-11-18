
package com.taskforge.service;

import com.taskforge.model.Task;
import com.taskforge.model.Priority;
import com.taskforge.model.TaskStatus;
import com.taskforge.repository.TaskRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskService {

    private final Map<String, Task> tasks;
    private final TaskRepository repository;
    private final ProjectService projectService;

    public TaskService() {
        tasks = new LinkedHashMap<>();
        repository = null;
        projectService = null;
    }

    public TaskService(ProjectService projectService) {

        if (projectService == null) {
            throw new IllegalArgumentException(
                    "Project service cannot be null."
            );
        }

        this.projectService = projectService;
        this.repository = null;
        this.tasks = new LinkedHashMap<>();
    }

    public TaskService(TaskRepository repository) {

        if (repository == null) {
            throw new IllegalArgumentException(
                    "Repository cannot be null."
            );
        }

        this.repository = repository;
        this.projectService = null;
        this.tasks = new LinkedHashMap<>();

        loadTasks();
    }

    public TaskService(
            TaskRepository repository,
            ProjectService projectService) {

        if (repository == null || projectService == null) {
            throw new IllegalArgumentException(
                    "Repository and project service cannot be null."
            );
        }

        this.repository = repository;
        this.projectService = projectService;
        this.tasks = new LinkedHashMap<>();

        loadTasks();
    }

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

    private void saveTasks() {

        if (repository != null) {
            repository.saveAll(
                    List.copyOf(tasks.values())
            );
        }
    }

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

    public List<Task> getAllTasks() {
        return List.copyOf(tasks.values());
    }

    public Optional<Task> findTaskById(String taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    // PROJECT MANAGEMENT

    public boolean assignTaskToProject(
            String taskId,
            String projectId) {

        if (projectService == null) {
            throw new IllegalStateException(
                    "Project service is not configured."
            );
        }

        Optional<Task> taskResult = findTaskById(taskId);

        if (taskResult.isEmpty()) {
            return false;
        }

        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException(
                    "Project ID cannot be empty."
            );
        }

        if (projectService.findProjectById(projectId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Project does not exist."
            );
        }

        taskResult.get().setProjectId(projectId);

        saveTasks();

        return true;
    }

    public List<Task> getTasksByProjectId(String projectId) {

        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException(
                    "Project ID cannot be empty."
            );
        }

        return tasks.values()
                .stream()
                .filter(task -> projectId.equals(task.getProjectId()))
                .collect(Collectors.toList());
    }

    public boolean removeTaskFromProject(String taskId) {

        Optional<Task> result = findTaskById(taskId);

        if (result.isEmpty()) {
            return false;
        }

        result.get().setProjectId(null);

        saveTasks();

        return true;
    }

    // DEPENDENCY MANAGEMENT

    // Add a prerequisite task
    public boolean addDependency(
            String taskId,
            String dependencyId) {

        Task task = tasks.get(taskId);
        Task dependency = tasks.get(dependencyId);

        if (task == null || dependency == null) {
            return false;
        }

        if (taskId.equals(dependencyId)) {
            throw new IllegalArgumentException(
                    "A task cannot depend on itself."
            );
        }

        if (task.getDependencies().contains(dependencyId)) {
            return false;
        }

        // Temporarily add dependency
        task.addDependency(dependencyId);

        // Check whether this creates a cycle
        if (hasCircularDependency()) {

            task.removeDependency(dependencyId);

            throw new IllegalArgumentException(
                    "Circular dependency detected!"
            );
        }

        saveTasks();

        return true;
    }

    // Remove a prerequisite
    public boolean removeDependency(
            String taskId,
            String dependencyId) {

        Task task = tasks.get(taskId);

        if (task == null) {
            return false;
        }

        boolean removed = task.removeDependency(dependencyId);

        if (removed) {
            saveTasks();
        }

        return removed;
    }

    // Find all tasks that are blocked
    public List<Task> getBlockedTasks() {

        return tasks.values()
                .stream()
                .filter(this::isTaskBlocked)
                .toList();
    }

    // Find tasks ready to execute
    public List<Task> getReadyTasks() {

        return tasks.values()
                .stream()
                .filter(task ->
                        task.getStatus() != TaskStatus.COMPLETED
                        && task.getStatus() != TaskStatus.CANCELLED
                        && !isTaskBlocked(task)
                )
                .toList();
    }

    // Check whether a particular task is blocked
    public boolean isTaskBlocked(Task task) {

        if (task == null) {
            return false;
        }

        for (String dependencyId : task.getDependencies()) {

            Task dependency = tasks.get(dependencyId);

            if (dependency == null
                    || dependency.getStatus() != TaskStatus.COMPLETED) {

                return true;
            }
        }

        return false;
    }

    // Detect circular dependencies using DFS
    public boolean hasCircularDependency() {

        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String taskId : tasks.keySet()) {

            if (detectCycle(
                    taskId,
                    visited,
                    recursionStack)) {

                return true;
            }
        }

        return false;
    }

    private boolean detectCycle(
            String taskId,
            Set<String> visited,
            Set<String> recursionStack) {

        if (recursionStack.contains(taskId)) {
            return true;
        }

        if (visited.contains(taskId)) {
            return false;
        }

        visited.add(taskId);
        recursionStack.add(taskId);

        Task task = tasks.get(taskId);

        if (task != null) {

            for (String dependencyId : task.getDependencies()) {

                if (detectCycle(
                        dependencyId,
                        visited,
                        recursionStack)) {

                    return true;
                }
            }
        }

        recursionStack.remove(taskId);

        return false;
    }

    // Return execution order using topological sorting
    public List<Task> getExecutionOrder() {

        if (hasCircularDependency()) {
            throw new IllegalStateException(
                    "Cannot calculate execution order because "
                    + "circular dependencies exist."
            );
        }

        Map<String, Integer> dependencyCounts =
                new LinkedHashMap<>();

        Map<String, List<String>> dependents =
                new LinkedHashMap<>();

        for (Task task : tasks.values()) {

            dependencyCounts.put(
                    task.getId(),
                    task.getDependencies().size()
            );

            dependents.put(
                    task.getId(),
                    new ArrayList<>()
            );
        }

        for (Task task : tasks.values()) {

            for (String dependencyId : task.getDependencies()) {

                dependents.get(dependencyId)
                        .add(task.getId());
            }
        }

        List<Task> executionOrder = new ArrayList<>();
        List<String> readyQueue = new ArrayList<>();

        for (Map.Entry<String, Integer> entry
                : dependencyCounts.entrySet()) {

            if (entry.getValue() == 0) {
                readyQueue.add(entry.getKey());
            }
        }

        int index = 0;

        while (index < readyQueue.size()) {

            String currentId = readyQueue.get(index++);

            executionOrder.add(tasks.get(currentId));

            for (String dependentId
                    : dependents.get(currentId)) {

                int remaining = dependencyCounts.get(dependentId) - 1;

                dependencyCounts.put(dependentId, remaining);

                if (remaining == 0) {
                    readyQueue.add(dependentId);
                }
            }
        }

        return executionOrder;
    }

    // DELETE TASK

    public boolean deleteTask(String taskId) {

        if (!tasks.containsKey(taskId)) {
            return false;
        }

        // Do not delete a task that other tasks depend on
        boolean hasDependents = tasks.values()
                .stream()
                .anyMatch(task ->
                        task.getDependencies().contains(taskId)
                );

        if (hasDependents) {
            throw new IllegalStateException(
                    "Cannot delete this task because other tasks "
                    + "depend on it. Remove those dependencies first."
            );
        }

        tasks.remove(taskId);

        saveTasks();

        return true;
    }

    // UPDATE TASK

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
