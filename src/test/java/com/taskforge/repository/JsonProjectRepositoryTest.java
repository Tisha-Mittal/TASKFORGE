package com.taskforge.repository;

import com.taskforge.model.Project;
import com.taskforge.model.ProjectStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonProjectRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonProjectRepository repository;
    private Path filePath;

    @BeforeEach
    void setUp() {

        filePath = tempDir.resolve("projects.json");
        repository = new JsonProjectRepository(filePath.toString());
    }

    @Test
    void shouldSaveAndLoadProjects() {

        Project project1 = new Project(
                "Project One", "Description one",
                LocalDate.of(2026, 12, 31)
        );
        Project project2 = new Project(
                "Project Two", "Description two", null
        );

        repository.saveAll(List.of(project1, project2));

        List<Project> loaded = repository.findAll();

        assertEquals(2, loaded.size());
        assertEquals("Project One", loaded.get(0).getName());
        assertEquals("Project Two", loaded.get(1).getName());
    }

    @Test
    void shouldReturnEmptyListWhenFileDoesNotExist() {

        List<Project> loaded = repository.findAll();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void shouldFindProjectById() {

        Project project = new Project(
                "Find Me", "Description", LocalDate.of(2027, 1, 31)
        );
        repository.saveAll(List.of(project));

        Optional<Project> found = repository.findById(project.getId());

        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenProjectNotFound() {

        Optional<Project> found = repository.findById("NONEXISTENT");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteProjectById() {

        Project project1 = new Project(
                "Keep Me", "", LocalDate.of(2026, 12, 31)
        );
        Project project2 = new Project(
                "Delete Me", "", null
        );
        repository.saveAll(List.of(project1, project2));

        repository.deleteById(project2.getId());

        List<Project> remaining = repository.findAll();

        assertEquals(1, remaining.size());
        assertEquals("Keep Me", remaining.get(0).getName());
        assertFalse(
                remaining.stream().anyMatch(p -> p.getId().equals(project2.getId()))
        );
    }

    @Test
    void shouldUpdateProjectAfterReload() {

        Project project = new Project(
                "Original Name", "", LocalDate.of(2026, 12, 31)
        );
        repository.saveAll(List.of(project));

        project.setName("Updated Name");
        project.setStatus(ProjectStatus.COMPLETED);
        repository.saveAll(List.of(project));

        List<Project> reloaded = repository.findAll();

        assertEquals(1, reloaded.size());
        assertEquals("Updated Name", reloaded.get(0).getName());
        assertEquals(ProjectStatus.COMPLETED, reloaded.get(0).getStatus());
    }
}
