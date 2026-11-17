
package com.taskforge.service;

import com.taskforge.model.Task;
import com.taskforge.model.Priority;
import com.taskforge.model.TaskStatus;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    @Test
    void shouldAddTask() {

        TaskService service = new TaskService();

        Task task = new Task(
                "Study Java",
                "Complete OOP",
                Priority.HIGH,
                LocalDate.now()
        );

        service.addTask(task);

        assertEquals(1, service.getAllTasks().size());
    }

    @Test
    void shouldFindTaskById() {

        TaskService service = new TaskService();

        Task task = new Task("Study Java", "", Priority.HIGH, null);

        service.addTask(task);

        Optional<Task> result = service.findTaskById(task.getId());

        assertTrue(result.isPresent());
        assertEquals("Study Java", result.get().getTitle());
    }

    @Test
    void shouldReturnEmptyWhenTaskDoesNotExist() {

        TaskService service = new TaskService();

        Optional<Task> result = service.findTaskById("INVALID-ID");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteTask() {

        TaskService service = new TaskService();

        Task task = new Task("Study Java", "", Priority.HIGH, null);

        service.addTask(task);

        boolean result = service.deleteTask(task.getId());

        assertTrue(result);
        assertTrue(service.getAllTasks().isEmpty());
    }

    @Test
    void shouldUpdateTask() {

        TaskService service = new TaskService();

        Task task = new Task("Old Title", "", Priority.LOW, null);

        service.addTask(task);

        boolean result = service.updateTask(
                task.getId(),
                "New Title",
                "Updated description",
                Priority.CRITICAL,
                LocalDate.of(2026, 12, 15)
        );

        assertTrue(result);
        assertEquals("New Title", task.getTitle());
        assertEquals(Priority.CRITICAL, task.getPriority());
    }

    @Test
    void shouldUpdateTaskStatus() {

        TaskService service = new TaskService();

        Task task = new Task("Study Java", "", Priority.HIGH, null);

        service.addTask(task);

        boolean result = service.updateTaskStatus(
                task.getId(),
                TaskStatus.IN_PROGRESS
        );

        assertTrue(result);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void shouldRejectNullTask() {

        TaskService service = new TaskService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addTask(null)
        );
    }
}
