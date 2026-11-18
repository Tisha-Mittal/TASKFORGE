
package com.taskforge.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskProjectAssignmentTest {

    @Test
    void shouldAssignTaskToProject() {

        Task task = new Task(
                "Build dashboard",
                "Create the project dashboard",
                Priority.HIGH,
                LocalDate.of(2026, 12, 31)
        );

        task.setProjectId("PRJ-12345678");

        assertEquals("PRJ-12345678", task.getProjectId());
    }

    @Test
    void shouldAllowTaskWithoutProject() {

        Task task = new Task(
                "Study Java",
                "Practice OOP concepts",
                Priority.MEDIUM,
                null
        );

        assertNull(task.getProjectId());
    }

    @Test
    void shouldRemoveProjectAssignmentWhenSetToNull() {

        Task task = new Task(
                "Build API",
                "Develop backend API",
                Priority.HIGH,
                null
        );

        task.setProjectId("PRJ-12345678");
        task.setProjectId(null);

        assertNull(task.getProjectId());
    }

    @Test
    void shouldTrimProjectId() {

        Task task = new Task(
                "Build UI",
                "Develop user interface",
                Priority.LOW,
                null
        );

        task.setProjectId("  PRJ-12345678  ");

        assertEquals("PRJ-12345678", task.getProjectId());
    }
}
