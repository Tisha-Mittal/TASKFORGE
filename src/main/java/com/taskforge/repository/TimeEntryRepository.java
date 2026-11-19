
package com.taskforge.repository;

import com.taskforge.model.TimeEntry;

import java.util.List;
import java.util.Optional;

public interface TimeEntryRepository {

    void saveAll(List<TimeEntry> timeEntries);

    List<TimeEntry> findAll();

    Optional<TimeEntry> findById(String timeEntryId);

    void deleteById(String timeEntryId);

    List<TimeEntry> findByTaskId(String taskId);

    List<TimeEntry> findRunningEntries();
}
