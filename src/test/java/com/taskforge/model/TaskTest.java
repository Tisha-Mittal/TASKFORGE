package com.taskforge.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void shouldCreateTaskSuccessfully() {

        LocalDate deadline = LocalDate.of(2026, 12, 1);

        Task task = new Task(
                "Complete Java Project",
                "Build TASKFORGE",
                Priority.HIGH,
                deadline
        );

        assertNotNull(task.getId());
        assertEquals("Complete Java Project", task.getTitle());
        assertEquals("Build TASKFORGE", task.getDescription());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(deadline, task.getDeadline());
        assertNotNull(task.getCreatedAt());
    }

    @Test
    void shouldGenerateDifferentIds() {

        Task task1 = new Task("Task One", "", Priority.LOW, null);
        Task task2 = new Task("Task Two", "", Priority.HIGH, null);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void shouldRejectEmptyTitle() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Task("", "Description", Priority.HIGH, null)
        );
    }

    @Test
    void shouldRejectNullPriority() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Task("Task", "Description", null, null)
        );
    }

    @Test
    void shouldUpdateTaskProperties() {

        Task task = new Task("Old Title", "", Priority.LOW, null);

        task.setTitle("New Title");
        task.setDescription("Updated Description");
        task.setPriority(Priority.CRITICAL);
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals("New Title", task.getTitle());
        assertEquals("Updated Description", task.getDescription());
        assertEquals(Priority.CRITICAL, task.getPriority());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }
}