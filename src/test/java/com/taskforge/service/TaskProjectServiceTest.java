
package com.taskforge.service;

import com.taskforge.model.Project;
import com.taskforge.model.Task;
import com.taskforge.model.Priority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskProjectServiceTest {

    private TaskService taskService;
    private ProjectService projectService;

    private Task task;
    private Project project;

    @BeforeEach
    void setUp() {

        projectService = new ProjectService();

        taskService = new TaskService(projectService);

        task = new Task(
                "Build dashboard",
                "Create the project dashboard",
                Priority.HIGH,
                null
        );

        project = new Project(
                "TASKFORGE",
                "Java productivity management application",
                null
        );

        taskService.addTask(task);
        projectService.addProject(project);
    }

    @Test
    void shouldAssignTaskToProject() {

        boolean assigned = taskService.assignTaskToProject(
                task.getId(),
                project.getId()
        );

        assertTrue(assigned);

        assertEquals(
                project.getId(),
                task.getProjectId()
        );
    }

    @Test
    void shouldRejectInvalidProjectId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.assignTaskToProject(
                        task.getId(),
                        "INVALID-PROJECT"
                )
        );

        assertNull(task.getProjectId());
    }

    @Test
    void shouldReturnFalseForNonExistingTask() {

        boolean assigned = taskService.assignTaskToProject(
                "INVALID-TASK",
                project.getId()
        );

        assertFalse(assigned);
    }

    @Test
    void shouldRetrieveTasksBelongingToProject() {

        taskService.assignTaskToProject(
                task.getId(),
                project.getId()
        );

        assertEquals(
                1,
                taskService.getTasksByProjectId(
                        project.getId()
                ).size()
        );
    }
}

