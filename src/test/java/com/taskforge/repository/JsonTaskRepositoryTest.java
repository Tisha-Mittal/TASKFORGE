package com.taskforge.repository;

import com.taskforge.model.Task;
import com.taskforge.model.Priority;
import com.taskforge.model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonTaskRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonTaskRepository repository;
    private Path filePath;

    @BeforeEach
    void setUp() {

        filePath = tempDir.resolve("tasks.json");
        repository = new JsonTaskRepository(filePath.toString());
    }

    @Test
    void shouldSaveAndLoadTasks() {

        Task task1 = new Task(
                "Task One", "Description one", Priority.HIGH,
                LocalDate.of(2026, 12, 1)
        );
        Task task2 = new Task(
                "Task Two", "Description two", Priority.MEDIUM, null
        );

        repository.saveAll(List.of(task1, task2));

        List<Task> loaded = repository.findAll();

        assertEquals(2, loaded.size());
        assertEquals("Task One", loaded.get(0).getTitle());
        assertEquals("Task Two", loaded.get(1).getTitle());
    }

    @Test
    void shouldReturnEmptyListWhenFileDoesNotExist() {

        List<Task> loaded = repository.findAll();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void shouldFindTaskById() {

        Task task = new Task(
                "Find Me", "Description", Priority.CRITICAL, null
        );
        repository.saveAll(List.of(task));

        Optional<Task> found = repository.findById(task.getId());

        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getTitle());
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {

        Optional<Task> found = repository.findById("NONEXISTENT");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteTaskById() {

        Task task1 = new Task(
                "Keep Me", "", Priority.LOW, null
        );
        Task task2 = new Task(
                "Delete Me", "", Priority.HIGH, null
        );
        repository.saveAll(List.of(task1, task2));

        repository.deleteById(task2.getId());

        List<Task> remaining = repository.findAll();

        assertEquals(1, remaining.size());
        assertEquals("Keep Me", remaining.get(0).getTitle());
        assertFalse(
                remaining.stream().anyMatch(t -> t.getId().equals(task2.getId()))
        );
    }

    @Test
    void shouldUpdateTaskAfterReload() {

        Task task = new Task(
                "Original Title", "", Priority.LOW, null
        );
        repository.saveAll(List.of(task));

        task.setTitle("Updated Title");
        task.setStatus(TaskStatus.COMPLETED);
        repository.saveAll(List.of(task));

        List<Task> reloaded = repository.findAll();

        assertEquals(1, reloaded.size());
        assertEquals("Updated Title", reloaded.get(0).getTitle());
        assertEquals(TaskStatus.COMPLETED, reloaded.get(0).getStatus());
    }
}
