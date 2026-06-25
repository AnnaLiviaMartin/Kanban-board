package de.hsrm.master.concurrency.kanbanboard.task.dto;

import de.hsrm.master.concurrency.kanbanboard.task.entity.Task;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        int position,
        long version,
        Long columnId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TaskResponse from(Task t) {
        return new TaskResponse(
                t.getId(), t.getTitle(), t.getDescription(), t.getPosition(), t.getVersion(),
                t.getColumn() != null ? t.getColumn().getId() : null,
                t.getCreatedAt(), t.getUpdatedAt());
    }
}
