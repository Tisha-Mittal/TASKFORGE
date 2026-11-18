
package com.taskforge.service;

import com.taskforge.model.Project;
import com.taskforge.model.ProjectStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    private ProjectService projectService;
    private Project project;

    @BeforeEach
    void setUp() {

        projectService = new ProjectService();

        project = new Project(
                "TASKFORGE",
                "Java productivity management application",
                LocalDate.of(2026, 12, 31)
        );
    }

    @Test
    void shouldAddProject() {

        projectService.addProject(project);

        assertEquals(1, projectService.getAllProjects().size());
    }

    @Test
    void shouldFindProjectById() {

        projectService.addProject(project);

        assertTrue(
                projectService.findProjectById(project.getId()).isPresent()
        );
    }

    @Test
    void shouldReturnEmptyWhenProjectDoesNotExist() {

        assertTrue(
                projectService.findProjectById("INVALID-ID").isEmpty()
        );
    }

    @Test
    void shouldDeleteProject() {

        projectService.addProject(project);

        boolean deleted = projectService.deleteProject(project.getId());

        assertTrue(deleted);
        assertTrue(projectService.getAllProjects().isEmpty());
    }

    @Test
    void shouldUpdateProject() {

        projectService.addProject(project);

        boolean updated = projectService.updateProject(
                project.getId(),
                "TASKFORGE V2",
                "Updated project description",
                LocalDate.of(2027, 1, 31)
        );

        assertTrue(updated);

        Project updatedProject =
                projectService.findProjectById(project.getId()).get();

        assertEquals("TASKFORGE V2", updatedProject.getName());
        assertEquals(
                "Updated project description",
                updatedProject.getDescription()
        );
        assertEquals(
                LocalDate.of(2027, 1, 31),
                updatedProject.getDeadline()
        );
    }

    @Test
    void shouldUpdateProjectStatus() {

        projectService.addProject(project);

        boolean updated = projectService.updateProjectStatus(
                project.getId(),
                ProjectStatus.COMPLETED
        );

        assertTrue(updated);

        assertEquals(
                ProjectStatus.COMPLETED,
                project.getStatus()
        );
    }

    @Test
    void shouldNotAddNullProject() {

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.addProject(null)
        );
    }

    @Test
    void shouldRejectEmptyProjectName() {

        projectService.addProject(project);

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.updateProject(
                        project.getId(),
                        "",
                        "Description",
                        LocalDate.now()
                )
        );
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingProject() {

        assertFalse(
                projectService.deleteProject("INVALID-ID")
        );
    }
}
