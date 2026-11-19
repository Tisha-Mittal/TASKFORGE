
package com.taskforge.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.taskforge.model.TimeEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonTimeEntryRepository implements TimeEntryRepository {

    private final ObjectMapper objectMapper;
    private final Path filePath;

    public JsonTimeEntryRepository(String fileName) {

        this.filePath = Path.of(fileName);

        this.objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
    }

    @Override
    public void saveAll(List<TimeEntry> timeEntries) {

        try {

            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), timeEntries);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save time entries: " + e.getMessage(), e
            );
        }
    }

    @Override
    public List<TimeEntry> findAll() {

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {

            return objectMapper.readValue(
                    filePath.toFile(),
                    new TypeReference<List<TimeEntry>>() {}
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to load time entries: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<TimeEntry> findById(String timeEntryId) {

        return findAll().stream()
                .filter(entry -> entry.getId().equals(timeEntryId))
                .findFirst();
    }

    @Override
    public void deleteById(String timeEntryId) {

        List<TimeEntry> entries = findAll();

        entries.removeIf(entry -> entry.getId().equals(timeEntryId));

        saveAll(entries);
    }

    @Override
    public List<TimeEntry> findByTaskId(String taskId) {

        return findAll().stream()
                .filter(entry -> taskId.equals(entry.getTaskId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeEntry> findRunningEntries() {

        return findAll().stream()
                .filter(TimeEntry::isRunning)
                .collect(Collectors.toList());
    }
}
