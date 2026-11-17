
package com.taskforge.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.taskforge.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonTaskRepository implements TaskRepository {

    private final ObjectMapper objectMapper;
    private final Path filePath;

    public JsonTaskRepository(String fileName) {

        this.filePath = Path.of(fileName);

        this.objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
    }

    @Override
    public void saveAll(List<Task> tasks) {

        try {

            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), tasks);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save tasks: " + e.getMessage(), e
            );
        }
    }

    @Override
    public List<Task> findAll() {

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {

            return objectMapper.readValue(
                    filePath.toFile(),
                    new TypeReference<List<Task>>() {}
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to load tasks: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<Task> findById(String taskId) {

        return findAll().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst();
    }

    @Override
    public void deleteById(String taskId) {

        List<Task> tasks = findAll();

        tasks.removeIf(task -> task.getId().equals(taskId));

        saveAll(tasks);
    }
}
