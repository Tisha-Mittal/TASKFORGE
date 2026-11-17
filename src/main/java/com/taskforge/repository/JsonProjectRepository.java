
package com.taskforge.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.taskforge.model.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonProjectRepository implements ProjectRepository {

    private final ObjectMapper objectMapper;
    private final Path filePath;

    public JsonProjectRepository(String fileName) {

        this.filePath = Path.of(fileName);

        this.objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
    }

    @Override
    public void saveAll(List<Project> projects) {

        try {

            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), projects);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save projects: " + e.getMessage(), e
            );
        }
    }

    @Override
    public List<Project> findAll() {

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {

            return objectMapper.readValue(
                    filePath.toFile(),
                    new TypeReference<List<Project>>() {}
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to load projects: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<Project> findById(String projectId) {

        return findAll().stream()
                .filter(project -> project.getId().equals(projectId))
                .findFirst();
    }

    @Override
    public void deleteById(String projectId) {

        List<Project> projects = findAll();

        projects.removeIf(
                project -> project.getId().equals(projectId)
        );

        saveAll(projects);
    }
}

